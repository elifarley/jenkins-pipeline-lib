package com.orgecc.jpl

def shc(String commands) { sh(returnStdout: true, script: "${commands}").trim() }

def getCommitId() { shc 'git rev-parse HEAD' }

def shManaged(String scriptID) {
  configFileProvider([configFile(fileId: "org.jenkinsci.plugins.managedscripts.${scriptID}", targetLocation: "/tmp/${scriptID}.sh", variable: 'script')]) {
    sh '. $script'
  }
}

def dockerizeProject() { sh """
set -x
mkdir -p .~/shell-lib
curl -H 'Cache-Control: no-cache' -fsSL https://github.com/elifarley/shell-lib/archive/master.tar.gz | \
tar -zx --strip-components 1 -C .~/shell-lib && \
chmod +x .~/shell-lib/bin/* && PATH="$PWD/.~/shell-lib/bin:$PATH"

test -e target || mkdir -p target 

DEBUG=1 dockerize-project

"""
  archiveArtifacts artifacts: 'target/app.tgz'
}

/**
Example:
import com.orgecc.jpl.Base
def jplb = new com.orgecc.jpl.Base()

withEnv(["GIT_COMMIT=${jplb.getCommitId()}", "DOCKER_REPO=my-repo"]) {
  jplb.dockerPush()
}
*/
def dockerPush() { withEnv(["PATH=$WORKSPACE/.~/shell-lib/bin:${env.PATH}"]) { sh """
set -x
pwd

mkdir -p .~/shell-lib
curl -fsSL -H 'Cache-Control: no-cache' https://github.com/elifarley/shell-lib/archive/master.tar.gz | \
tar -zx --strip-components 1 -C .~/shell-lib && \
chmod +x .~/shell-lib/bin/*
echo PATH: $PATH
which jenkins-docker-push

DEBUG=1 jenkins-docker-push \
"${env.JOB_NAME}" "${env.BUILD_NUMBER}" "${env.GIT_COMMIT}" "${env.DOCKER_REPO}"
"""}}
