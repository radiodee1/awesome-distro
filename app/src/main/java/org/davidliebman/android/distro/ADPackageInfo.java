package org.davidliebman.android.distro;

import java.util.Date;

/**
 * Created by dave on 4/27/17.
 */

public class ADPackageInfo {
    public String packageName = "";
    public String packageVersion = "";
    public String packageSection = "";
    public String packageFilename = "";
    public long   packageDate = 0;
    public boolean packageIsNew = false;

    @Override
    public String toString() {
        return packageName + " " + packageSection + " " + packageVersion + " " + new Date(packageDate);
    }
}
