package com.orgecc.jpl

def shc(String commands) { sh(returnStdout: true, script: "${commands}").trim() }

def getCommitId() { shc 'git rev-parse HEAD' }

def dockerizeProject(String scriptID) {
  configFileProvider([configFile(fileId: scriptID, targetLocation: '/tmp/$JOB_NAME/dockerize-project.sh', variable: 'script')]) {
    sh '. $script'
  }
}
