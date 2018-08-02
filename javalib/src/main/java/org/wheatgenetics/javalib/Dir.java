package org.wheatgenetics.javalib;

/**
 * Uses:
 * org.wheatgenetics.javalib.IndentationStack
 */
@java.lang.SuppressWarnings({"ClassExplicitlyExtendsObject"})
public class Dir extends java.lang.Object
{
    // region Types
    @java.lang.SuppressWarnings({"UnnecessaryEnumModifier"})
    private static enum PermissionCheck { PERMITTED, REQUESTED, DENIED }

    public abstract static class PermissionException extends java.lang.Exception
    { private PermissionException(final java.lang.String message) { super(message); } }

    public static class PermissionRequestedException
    extends org.wheatgenetics.javalib.Dir.PermissionException
    { private PermissionRequestedException() { super("Permission requested"); } }

    public static class PermissionDeniedException
    extends org.wheatgenetics.javalib.Dir.PermissionException
    { private PermissionDeniedException() { super("Permission denied"); } }
    // endregion

    // region Fields
    private final java.io.File                               path               ;
    private final java.lang.String                           blankHiddenFileName;
    private final org.wheatgenetics.javalib.IndentationStack indentationStack =
        new org.wheatgenetics.javalib.IndentationStack();

    private boolean permissionRequired, existsHasBeenSet = false, exists;
    // endregion

    // region Private Methods
    private static void throwIfNotPermitted(
    final org.wheatgenetics.javalib.Dir.PermissionCheck permissionCheck)
    throws org.wheatgenetics.javalib.Dir.PermissionException
    {
        switch (permissionCheck)
        {
            case REQUESTED: throw new org.wheatgenetics.javalib.Dir.PermissionRequestedException();
            case DENIED   : throw new org.wheatgenetics.javalib.Dir.PermissionDeniedException   ();
        }
    }

    private static boolean createNewDir(final java.io.File parent, final java.lang.String child)
    {
        if (null == parent)
            return false;
        else
        {
            final java.io.File dir = null == child ? parent : new java.io.File(parent, child);
            // noinspection SimplifiableConditionalExpression
            return dir.exists() ? false : dir.mkdirs();
        }
    }

    private java.lang.String status()
    {
        return java.lang.String.format(
            "permissionRequired: %b, existsHasBeenSet: %b, exists: %b",
            this.permissionRequired, this.existsHasBeenSet, this.exists);
    }

    private org.wheatgenetics.javalib.Dir.PermissionCheck checkPermission()
    {
        this.indentationStack.push("checkPermission()");
        try
        {
            final java.lang.StringBuilder statusStringBuilder =
                new java.lang.StringBuilder(this.status());
            this.log("entered");
            try
            {
                if (this.permissionRequired)
                {
                    final boolean permissionGranted = this.permissionGranted();
                    statusStringBuilder.append(java.lang.String.format(
                        ", permissionGranted(): %b", permissionGranted));
                    if (permissionGranted)
                        return org.wheatgenetics.javalib.Dir.PermissionCheck.PERMITTED;
                    else
                    {
                        final boolean requestPermission = requestPermission();
                        statusStringBuilder.append(java.lang.String.format(
                            ", requestPermission(): %b", requestPermission));
                        return requestPermission ?
                            org.wheatgenetics.javalib.Dir.PermissionCheck.REQUESTED :
                            org.wheatgenetics.javalib.Dir.PermissionCheck.DENIED    ;
                    }
                }
                else return org.wheatgenetics.javalib.Dir.PermissionCheck.PERMITTED;
            }
            finally { this.log("leaving: " + statusStringBuilder.toString()); }
        }
        finally { this.indentationStack.pop(); }
    }

    private org.wheatgenetics.javalib.Dir.PermissionCheck setExists()
    {
        this.indentationStack.push("setExists()");
        try
        {
            this.log("entered");
            try
            {
                final org.wheatgenetics.javalib.Dir.PermissionCheck result = this.checkPermission();
                if (org.wheatgenetics.javalib.Dir.PermissionCheck.PERMITTED == result)
                {
                    final java.io.File path = this.getPath();
                    assert null != path; this.exists = path.exists();
                    this.existsHasBeenSet = true;
                }
                return result;
            }
            finally { this.log("leaving"); }
        }
        finally { this.indentationStack.pop(); }
    }

    private void setPermissionRequired(final boolean permissionRequired)
    { this.permissionRequired = permissionRequired; }
    // endregion

    void setPermissionRequiredToTrue() { this.setPermissionRequired(true); }

    // region Protected Methods
    protected java.io.File     getPath          ()                           { return this.path; }
    protected void             log              (final java.lang.String msg) {                   }
    protected java.lang.String label            ()       { return this.indentationStack.label(); }
    protected boolean          permissionGranted()                           { return false    ; }

    /**
     * The purpose of this method is to request permission.  In this class the method doesn't do its
     * job (because it can't -- this must be done at the Android (not the Java) layer).  Since it
     * doesn't do its job it returns the value false.  When this method is overridden the subclass
     * that makes this method do its job should return the value true.
     */
    protected boolean requestPermission()
    {
        // noinspection UnnecessaryLocalVariable
        final boolean permissionRequested = false; return permissionRequested;
    }
    // endregion

    // region Constructors
    public Dir(final java.io.File path, final java.lang.String blankHiddenFileName)
    {
        super();

        if (null == path) throw new java.lang.IllegalArgumentException("path must not be null");
        this.path = path; this.blankHiddenFileName = blankHiddenFileName;
        this.setPermissionRequired(false);
    }

    public Dir(final java.io.File parent, final java.lang.String child,
    final java.lang.String blankHiddenFileName)
    { this(new java.io.File(parent, child), blankHiddenFileName); }

    public Dir(final org.wheatgenetics.javalib.Dir parent, final java.lang.String child)
    { this(parent.getPath(), child, parent.blankHiddenFileName); }
    // endregion

    // region Public Methods
    public java.lang.String getPathAsString()
    { final java.io.File path = this.getPath(); return null == path ? null : path.getPath(); }

    public boolean getExists() throws org.wheatgenetics.javalib.Dir.PermissionException
    {
        this.indentationStack.push("getExists()");
        try
        {
            this.log("entered: " + this.status());
            try
            {
                if (!this.existsHasBeenSet)
                    org.wheatgenetics.javalib.Dir.throwIfNotPermitted(this.setExists());   // throws
                return this.exists;
            }
            finally { this.log("leaving: " + this.status()); }
        }
        finally { this.indentationStack.pop(); }
    }

    public java.io.File createIfMissing()
    throws java.io.IOException, org.wheatgenetics.javalib.Dir.PermissionException
    {
        this.indentationStack.push("createIfMissing()");
        try
        {
            this.log("entered");
            try
            {
                final java.io.File path = this.getPath();
                if (!this.getExists())   // throws org.wheatgenetics.javalib.Dir.PermissionException
                {
                    org.wheatgenetics.javalib.Dir.throwIfNotPermitted(                     // throws
                        this.checkPermission());
                    org.wheatgenetics.javalib.Dir.createNewDir(path,null);
                    org.wheatgenetics.javalib.Dir.throwIfNotPermitted(this.setExists());   // throws
                }

                if (!this.getExists())   // throws org.wheatgenetics.javalib.Dir.PermissionException
                    throw new java.io.IOException(this.getPathAsString() + " does not exist");
                else
                    if (null == this.blankHiddenFileName)
                        return null;
                    else
                        if (this.blankHiddenFileName.trim().length() <= 0)
                            return null;
                        else
                        {
                            final java.io.File blankHiddenFile =
                                new java.io.File(path, this.blankHiddenFileName);

                            org.wheatgenetics.javalib.Dir.throwIfNotPermitted(             // throws
                                this.checkPermission());

                            if (!blankHiddenFile.exists())
                                // noinspection ResultOfMethodCallIgnored
                                blankHiddenFile.createNewFile();       // throws java.io.IOException

                            return blankHiddenFile;
                        }
            }
            finally { this.log("leaving"); }
        }
        finally { this.indentationStack.pop(); }
    }

    public java.io.File makeFile(final java.lang.String fileName)
    throws java.io.IOException, org.wheatgenetics.javalib.Dir.PermissionException
    {
        if (this.getExists())            // throws org.wheatgenetics.javalib.Dir.PermissionException
            return new java.io.File(this.getPath(), fileName);
        else
            throw new java.io.IOException(this.getPathAsString() + " does not exist");
    }

    public java.io.File createNewFile(final java.lang.String fileName)
    throws java.io.IOException, org.wheatgenetics.javalib.Dir.PermissionException
    {
        final java.io.File file = this.makeFile(fileName);            // throws java.io.IOException,
                                                                      //  PermissionException
        org.wheatgenetics.javalib.Dir.throwIfNotPermitted(this.checkPermission());         // throws

        assert null != file;
        // noinspection ResultOfMethodCallIgnored
        file.createNewFile();                                          // throws java.io.IOException

        return file;
    }

    public void createNewDir(final java.lang.String dirName)
    throws org.wheatgenetics.javalib.Dir.PermissionException
    {
        org.wheatgenetics.javalib.Dir.throwIfNotPermitted(this.checkPermission());         // throws
        org.wheatgenetics.javalib.Dir.createNewDir(this.getPath(), dirName);
    }

    public java.lang.String[] list() throws org.wheatgenetics.javalib.Dir.PermissionException
    {
        this.indentationStack.push("list()");
        try
        {
            this.log("entered");
            try
            {
                final java.io.File path = this.getPath();
                if (null == path)
                    return null;
                else
                    if (this.getExists())                          // throws org.wheatgenetics.java-
                    {                                              //  lib.Dir.PermissionException
                        org.wheatgenetics.javalib.Dir.throwIfNotPermitted(                 // throws
                            this.checkPermission());
                        return path.isDirectory() ? path.list() : null;
                    }
                    else return null;
            }
            finally { this.log("leaving"); }
        }
        finally { this.indentationStack.pop(); }
    }

    public java.lang.String[] list(final java.lang.String regex)
    throws org.wheatgenetics.javalib.Dir.PermissionException
    {
        final java.lang.String unfilteredList[] = this.list();     // throws org.wheatgenetics.java-
        if (null == unfilteredList)                                //   lib.Dir.PermissionException
            return null;
        else
            if (unfilteredList.length < 1)
                return unfilteredList;
            else
            {
                final java.util.ArrayList<java.lang.String> arrayList =
                    new java.util.ArrayList<java.lang.String>();
                {
                    final java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
                    for (final java.lang.String l: unfilteredList)
                        if (pattern.matcher(l).matches()) arrayList.add(l);
                }
                final java.lang.String filteredList[] = new java.lang.String[arrayList.size()];
                return arrayList.toArray(filteredList);
            }
    }
    // endregion
}