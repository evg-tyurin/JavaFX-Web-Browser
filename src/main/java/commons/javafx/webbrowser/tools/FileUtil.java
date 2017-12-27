package commons.javafx.webbrowser.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.ResourceBundle;

public class FileUtil
{
    private static HashMap<String, ResourceBundle> bundles = new HashMap<String, ResourceBundle>();

    public static byte[] read(InputStream in) throws IOException
    {
		ByteArrayOutputStream response = new ByteArrayOutputStream();
		copy(in,response);
		in.close();
        return response.toByteArray();
    }

    public static String lookFile(String[] dirs, String filename)
    {
        for (int i=0; i<dirs.length; i++) {
            String fname = dirs[i]+File.separatorChar+filename;
            File file = new File(fname);
            if (file.exists()) {
                return fname;
            }
        }
        return null;
    }

    public static void copyResource(String resource, String target) throws IOException
    {
        FileOutputStream targetFile = new FileOutputStream(target);
        InputStream in = FileUtil.class.getResourceAsStream(resource);
		int b = -1;
		while ((b=in.read())>-1) {
			targetFile.write(b);
		}
		in.close();
		targetFile.close();
    }
    /**
     * @param source
     * @param out
     * @throws IOException
     */
    public static void copyFile(File source, OutputStream out) throws IOException
    {
    	FileInputStream in = new FileInputStream(source);
    	copy(in,out);
    	in.close();
    }

    public static void copy(InputStream in, OutputStream out) throws IOException
    {
        int b = -1;
        int bufLen = 64*1024;
        byte[] buf = new byte[bufLen];
        while ((b=in.read(buf)) > 0)
        {
            out.write(buf,0,b);
            out.flush();
        }
    }
    public static void copyBytes(InputStream in, OutputStream out, long offset, long length) throws IOException
    {
        int b = -1;
        long bufLenL = 64*1024L;
        int bufLen = (int) bufLenL;
        byte[] buf = new byte[bufLen];
        long total = 0L;
        int len = buf.length;
        if (length-total<bufLenL)
        	len = (int) (length-total);

        in.skip(offset);
        while ((b=in.read(buf,0,len)) > 0)
        {
            out.write(buf,0,b);
            out.flush();
            total += b;
            if (length-total<bufLenL)
            	len = (int) (length-total);
        }
        if (total!=length)
        	throw new RuntimeException("Can't read specified number of bytes: read "+total+" instead of "+length);
    }
    public static void copyBytes(ObjectInput in, OutputStream out, long length) throws IOException
    {
        int b = -1;
        long bufLenL = 64*1024L;
        int bufLen = (int) bufLenL;
        byte[] buf = new byte[bufLen];
        long total = 0L;
        int len = buf.length;
        if (length-total<bufLenL)
        	len = (int) (length-total);

        while ((b=in.read(buf,0,len)) > 0)
        {
            out.write(buf,0,b);
            out.flush();
            total += b;
            if (length-total<bufLenL)
            	len = (int) (length-total);
        }
        if (total!=length)
        	throw new RuntimeException("Can't read specified number of bytes: read "+total+" instead of "+length);
    }
    public static void copy(InputStream in, ObjectOutput out) throws IOException
    {
        int b = -1;
        int bufLen = 64*1024;
        byte[] buf = new byte[bufLen];
        while ((b=in.read(buf)) > 0)
        {
            out.write(buf,0,b);
            out.flush();
        }
    }
    /**
     * @param in
     * @param targetFile
     * @throws IOException
     */
    public static void save2File(InputStream in, File targetFile) throws IOException
    {
        FileOutputStream out = new FileOutputStream(targetFile);
        copy(in,out);
		in.close();
		out.close();
    }
    
    public static String getProperty(String bundle, String key) {
        ResourceBundle b = bundles.get(bundle);
        if (b==null) { 
            b = ResourceBundle.getBundle(bundle);
            bundles.put(bundle, b);
        }
        return b.getString(key);
    }

    public static String padd4(int j)
    {
//        return padd4(j, "0000");
        String s = ""+j;
        while (s.length()<4) {
            s = "0"+s;
        }
        return s;
    }

    public static String padd4(int j, String prefix){
        int prefixSense = 4;
        if(prefix!=null) {
            prefixSense = prefix.length();
            if(prefixSense==0 || prefixSense>7) prefixSense = 4;
        }
        int sense = 8 - prefixSense;
        StringBuffer s = new StringBuffer();
        for(int i=0;i<sense-1;i++){
            s.append("0");
        }
        s.append(j);
        return s.toString();
    }

    /**
     * @param text
     * @param file
     * @throws IOException
     */
    public static void save2File(byte[] content, File file) throws IOException
    {
        FileOutputStream out = new FileOutputStream(file);
        out.write(content);
        out.close();
    }
    /**
     * @param fullpath
     * @throws Exception
     */
    public static void ensureDirExists(String fullpath) throws IOException
    {
        File f = new File(fullpath);
        if (!f.exists()) {
            if (!f.mkdirs()) {
                throw new IOException ("Can't create directory: "+fullpath);
            }
        }
    }

    public static String read(File file, String encoding) throws IOException
    {
        FileInputStream in = new FileInputStream(file);
        byte[] buf = new byte[in.available()];
        in.read(buf);
        in.close();
        return new String(buf,encoding);
    }

    public static String trimExt(String nameOrPath)
    {
        return nameOrPath.substring(0,nameOrPath.lastIndexOf("."));
    }

    public static String getExt(String name)
    {
        int pos = name.lastIndexOf('.');
        if (pos>=0) {
            return name.substring(pos+1);            
        }
        return "";
    }

    public static boolean rmdir(File dir)
    {
        if (!dir.exists()) return true;
        if (dir.isFile()) return dir.delete();
        
        File[] files = dir.listFiles();
        for (int i=0; i<files.length; i++) 
        {
            if (files[i].isDirectory())
            {
                rmdir(files[i]);
            }
            else 
            {
                files[i].delete();
            }
        }
        return dir.delete();
    }    

    public static String getNumberedDir(String parent) throws IOException
    {
        String tempDir = parent;
        for (int i=1; ; i++) {
            File dir = new File(tempDir+i+File.separator);
            if (!dir.exists()) {
                FileUtil.ensureDirExists(dir.getAbsolutePath());
                tempDir = dir.getAbsolutePath()+"/";
                break;
            }
        }
        return tempDir;
    }

    public static void copy(File src, File dst) throws IOException
    {
        InputStream from = new FileInputStream(src);
        OutputStream to = new FileOutputStream(dst);
        FileUtil.copy(from, to);
        from.close();
        to.close();
    }

}
