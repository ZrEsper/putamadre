"""Use the existing HTTPS proxy without copying credentials into arguments."""
import os,subprocess,sys,urllib.parse
from pathlib import Path
root=Path(__file__).resolve().parent.parent
env=os.environ.copy()
if not env.get('JAVA_HOME') and Path('/workspace/lrarmor-build-tools/jdk/jdk-21.0.12.1/bin/javac').exists():env['JAVA_HOME']='/workspace/lrarmor-build-tools/jdk/jdk-21.0.12.1'
env.setdefault('GRADLE_USER_HOME',str(Path('/workspace/lrarmor-gradle-cache')) if Path('/workspace').exists() else str(Path.home()/'.gradle'))
proxy=urllib.parse.urlparse(env.get('HTTPS_PROXY',''))
if proxy.hostname:
 env['JAVA_TOOL_OPTIONS']=' '.join([f'-D{scheme}.proxyHost={proxy.hostname} -D{scheme}.proxyPort={proxy.port or 80}' for scheme in ['https','http']])
 if Path('/etc/ssl/certs/java/cacerts').exists():env['JAVA_TOOL_OPTIONS']+=' -Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts'
sys.exit(subprocess.run([str(root/'gradlew'),'--no-daemon']+sys.argv[1:],cwd=root,env=env).returncode)
