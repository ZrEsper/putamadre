"""Use the existing HTTPS proxy without copying credentials into arguments."""
import os,subprocess,sys,urllib.parse
from pathlib import Path
root=Path(__file__).resolve().parent
env=os.environ.copy();env['JAVA_HOME']='/workspace/lrarmor-build-tools/jdk/jdk-21.0.12.1';env['GRADLE_USER_HOME']='/workspace/lrarmor-gradle-cache'
proxy=urllib.parse.urlparse(env.get('HTTPS_PROXY',''))
if proxy.hostname:
 env['JAVA_TOOL_OPTIONS']=' '.join([f'-D{scheme}.proxyHost={proxy.hostname} -D{scheme}.proxyPort={proxy.port or 80}' for scheme in ['https','http']])+' -Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts'
sys.exit(subprocess.run([str(root/'gradlew'),'--no-daemon']+sys.argv[1:],cwd=root,env=env).returncode)
