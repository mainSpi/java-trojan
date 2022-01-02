package greek.horse.models;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;

public class FileStruct implements Comparable<FileStruct>, Serializable {

    private final String path;
    private final String name;
    private final boolean isDrive;
    private final boolean isDir;
    private final String size;
    private final FileType fileType;
    private static final HashMap<String, FileType> extMap = createMap();

    public FileStruct(String name, long size, String path, boolean isDrive, boolean isDir) {
        this.path = path;
        this.name = name;
        this.isDrive = isDrive;
        this.isDir = isDir;

        if (size < Math.pow(10, 3)) {
            this.size = (int) (size) + " B";
        } else if (size < Math.pow(10, 6)) {
            this.size = (int) (size / Math.pow(10, 3)) + " KB";
        } else if (size < Math.pow(10, 9)) {
            this.size = (int) (size / Math.pow(10, 6)) + " MB";
        } else if (size < Math.pow(10, 12)) {
            this.size = (int) (size / Math.pow(10, 9)) + " GB";
        } else { // TB
            this.size = (int) (size / Math.pow(10, 12)) + " TB";
        }

        this.fileType = createFileType();

    }

    private FileType createFileType() {

        if (this.name.contentEquals("..") || isDir) {
            return FileType.DIRECTORY;
        } else if (isDrive) {
            return FileType.DRIVE;
        }

        String ext = getExtension(name).toLowerCase(Locale.ROOT);

        if (!extMap.containsKey(ext)) {
            return FileType.UNKNOWN;
        }

        return extMap.get(ext);

    }

    private static HashMap<String, FileType> createMap() {
        HashMap<String, FileType> map = new HashMap<>();
        map.put("3g2", FileType.VIDEO);
        map.put("3gp", FileType.VIDEO);
        map.put("aaf", FileType.VIDEO);
        map.put("asf", FileType.VIDEO);
        map.put("avchd", FileType.VIDEO);
        map.put("avi", FileType.VIDEO);
        map.put("drc", FileType.VIDEO);
        map.put("flv", FileType.VIDEO);
        map.put("m2v", FileType.VIDEO);
        map.put("m4p", FileType.VIDEO);
        map.put("m4v", FileType.VIDEO);
        map.put("mkv", FileType.VIDEO);
        map.put("mng", FileType.VIDEO);
        map.put("mov", FileType.VIDEO);
        map.put("mp2", FileType.VIDEO);
        map.put("mp4", FileType.VIDEO);
        map.put("mpe", FileType.VIDEO);
        map.put("mpeg", FileType.VIDEO);
        map.put("mpg", FileType.VIDEO);
        map.put("mpv", FileType.VIDEO);
        map.put("mxf", FileType.VIDEO);
        map.put("nsv", FileType.VIDEO);
        map.put("ogg", FileType.VIDEO);
        map.put("ogv", FileType.VIDEO);
        map.put("ogm", FileType.VIDEO);
        map.put("qt", FileType.VIDEO);
        map.put("rm", FileType.VIDEO);
        map.put("rmvb", FileType.VIDEO);
        map.put("roq", FileType.VIDEO);
        map.put("srt", FileType.VIDEO);
        map.put("svi", FileType.VIDEO);
        map.put("vob", FileType.VIDEO);
        map.put("webm", FileType.VIDEO);
        map.put("wmv", FileType.VIDEO);
        map.put("yuv", FileType.VIDEO);
        map.put("exe", FileType.EXECUTABLE);
        map.put("msi", FileType.EXECUTABLE);
        map.put("bin", FileType.EXECUTABLE);
        map.put("command", FileType.EXECUTABLE);
        map.put("sh", FileType.EXECUTABLE);
        map.put("bat", FileType.EXECUTABLE);
        map.put("crx", FileType.EXECUTABLE);
        map.put("aac", FileType.AUDIO);
        map.put("aiff", FileType.AUDIO);
        map.put("ape", FileType.AUDIO);
        map.put("au", FileType.AUDIO);
        map.put("flac", FileType.AUDIO);
        map.put("gsm", FileType.AUDIO);
        map.put("it", FileType.AUDIO);
        map.put("m3u", FileType.AUDIO);
        map.put("m4a", FileType.AUDIO);
        map.put("mid", FileType.AUDIO);
        map.put("mod", FileType.AUDIO);
        map.put("mp3", FileType.AUDIO);
        map.put("mpa", FileType.AUDIO);
        map.put("pls", FileType.AUDIO);
        map.put("ra", FileType.AUDIO);
        map.put("s3m", FileType.AUDIO);
        map.put("sid", FileType.AUDIO);
        map.put("wav", FileType.AUDIO);
        map.put("wma", FileType.AUDIO);
        map.put("xm", FileType.AUDIO);
        map.put("eot", FileType.FONT);
        map.put("otf", FileType.FONT);
        map.put("ttf", FileType.FONT);
        map.put("woff", FileType.FONT);
        map.put("woff2", FileType.FONT);
        map.put("3dm", FileType.IMAGE);
        map.put("3ds", FileType.IMAGE);
        map.put("max", FileType.IMAGE);
        map.put("bmp", FileType.IMAGE);
        map.put("dds", FileType.IMAGE);
        map.put("gif", FileType.IMAGE);
        map.put("jpg", FileType.IMAGE);
        map.put("jpeg", FileType.IMAGE);
        map.put("png", FileType.IMAGE);
        map.put("psd", FileType.IMAGE);
        map.put("xcf", FileType.IMAGE);
        map.put("tga", FileType.IMAGE);
        map.put("thm", FileType.IMAGE);
        map.put("tif", FileType.IMAGE);
        map.put("tiff", FileType.IMAGE);
        map.put("ai", FileType.IMAGE);
        map.put("eps", FileType.IMAGE);
        map.put("ps", FileType.IMAGE);
        map.put("svg", FileType.IMAGE);
        map.put("dwg", FileType.IMAGE);
        map.put("dxf", FileType.IMAGE);
        map.put("gpx", FileType.IMAGE);
        map.put("kml", FileType.IMAGE);
        map.put("kmz", FileType.IMAGE);
        map.put("webp", FileType.IMAGE);
        map.put("doc", FileType.TEXT);
        map.put("docx", FileType.TEXT);
        map.put("ebook", FileType.TEXT);
        map.put("log", FileType.TEXT);
        map.put("md", FileType.TEXT);
        map.put("msg", FileType.TEXT);
        map.put("odt", FileType.TEXT);
        map.put("org", FileType.TEXT);
        map.put("pages", FileType.TEXT);
        map.put("pdf", FileType.TEXT);
        map.put("rtf", FileType.TEXT);
        map.put("rst", FileType.TEXT);
        map.put("tex", FileType.TEXT);
        map.put("txt", FileType.TEXT);
        map.put("wpd", FileType.TEXT);
        map.put("wps", FileType.TEXT);
        map.put("7z", FileType.ZIP);
        map.put("a", FileType.ZIP);
        map.put("ar", FileType.ZIP);
        map.put("bz2", FileType.ZIP);
        map.put("cab", FileType.ZIP);
        map.put("cpio", FileType.ZIP);
        map.put("deb", FileType.ZIP);
        map.put("dmg", FileType.ZIP);
        map.put("egg", FileType.ZIP);
        map.put("gz", FileType.ZIP);
        map.put("iso", FileType.ZIP);
        map.put("jar", FileType.ZIP);
        map.put("lha", FileType.ZIP);
        map.put("mar", FileType.ZIP);
        map.put("pea", FileType.ZIP);
        map.put("rar", FileType.ZIP);
        map.put("rpm", FileType.ZIP);
        map.put("s7z", FileType.ZIP);
        map.put("shar", FileType.ZIP);
        map.put("tar", FileType.ZIP);
        map.put("tbz2", FileType.ZIP);
        map.put("tgz", FileType.ZIP);
        map.put("tlz", FileType.ZIP);
        map.put("war", FileType.ZIP);
        map.put("whl", FileType.ZIP);
        map.put("xpi", FileType.ZIP);
        map.put("zip", FileType.ZIP);
        map.put("zipx", FileType.ZIP);
        map.put("xz", FileType.ZIP);
        map.put("pak", FileType.ZIP);
        map.put("c", FileType.CODE);
        map.put("cc", FileType.CODE);
        map.put("class", FileType.CODE);
        map.put("clj", FileType.CODE);
        map.put("cpp", FileType.CODE);
        map.put("cs", FileType.CODE);
        map.put("cxx", FileType.CODE);
        map.put("el", FileType.CODE);
        map.put("go", FileType.CODE);
        map.put("h", FileType.CODE);
        map.put("java", FileType.CODE);
        map.put("lua", FileType.CODE);
        map.put("m", FileType.CODE);
        map.put("m4", FileType.CODE);
        map.put("php", FileType.CODE);
        map.put("pl", FileType.CODE);
        map.put("po", FileType.CODE);
        map.put("py", FileType.CODE);
        map.put("rb", FileType.CODE);
        map.put("rs", FileType.CODE);
        map.put("swift", FileType.CODE);
        map.put("vb", FileType.CODE);
        map.put("vcxproj", FileType.CODE);
        map.put("xcodeproj", FileType.CODE);
        map.put("xml", FileType.CODE);
        map.put("diff", FileType.CODE);
        map.put("patch", FileType.CODE);
        map.put("html", FileType.CODE);
        map.put("js", FileType.CODE);
        return map;
    }

    public String getExtension(String filename) {
        if (filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    @Override
    public int compareTo(@NotNull FileStruct o) {
        return this.name.compareToIgnoreCase(o.name);
    }

    @Override
    public String toString() {
        return "FileStruct{" +
                "uri=" + path +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                '}';
    }

    public FileType getFileType() {
        return fileType;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public boolean isDrive() {
        return isDrive;
    }

    public boolean isDir() {
        return isDir;
    }

    public String getSize() {
        return size;
    }
}
