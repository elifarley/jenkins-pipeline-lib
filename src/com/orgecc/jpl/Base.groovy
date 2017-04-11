def shc(String commands) { sh(returnStdout: true, script: commands.trim() }

def getCommitId() { shc 'git rev-parse HEAD' }
