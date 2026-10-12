import org.apache.maven.artifact.versioning.*;
public class VersionChecks {
 public static void main(String[] args)throws Exception{
  VersionRange range=VersionRange.createFromVersionSpec("[1.1.4.2,1.1.4.3)");
  for(String v:new String[]{"1.1.4.2","1.1.4.2-zomboid-storage.1","1.1.4.2-zomboid-storage.2"})if(!range.containsVersion(new DefaultArtifactVersion(v)))throw new AssertionError("Must accept "+v);
  for(String v:new String[]{"1.1.4.1","1.1.4.3","1.2.0"})if(range.containsVersion(new DefaultArtifactVersion(v)))throw new AssertionError("Must reject "+v);
  VersionRange exact=VersionRange.createFromVersionSpec("[1.1.4.2]");if(exact.containsVersion(new DefaultArtifactVersion("1.1.4.2-zomboid-storage.1")))throw new AssertionError("Original error not reproduced");
  System.out.println("Maven version checks passed: 7; original exact-range rejection reproduced");
 }
}
