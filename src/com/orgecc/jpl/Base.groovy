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

""" }

def dockerPush() { sh """
set -x
pwd

mkdir -p .~/shell-lib
curl -fsSL -H 'Cache-Control: no-cache' https://github.com/elifarley/shell-lib/archive/master.tar.gz | \
tar -zx --strip-components 1 -C .~/shell-lib && \
chmod +x .~/shell-lib/bin/* && PATH="$PWD/.~/shell-lib/bin:$PATH"

DEBUG=1 jenkins-docker-push \
"${UPSTREAM_JOB_NAME:-$JOB_NAME}" "${UPSTREAM_BUILD_NUMBER:-$BUILD_NUMBER}" "${UPSTREAM_GIT_COMMIT:-$GIT_COMMIT}" "$DOCKER_REPO"
""" }
