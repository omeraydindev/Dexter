package ma.dexter.tools.jar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import ma.dexter.BuildConfig;

public class JarPackager {
    private String input, output;
    private Attributes attributes;

    public JarPackager(File input, File output) {
        this(input, output, getDefAttrs());
    }

    public JarPackager(File input, File output, Attributes attributes) {
        this.input = input.getAbsolutePath();
        this.output = output.getAbsolutePath();
        this.attributes = attributes;
    }

    public void create() throws IOException {
        File classesFolder = new File(input);

        Manifest manifest = buildManifest(attributes);

        try (FileOutputStream stream = new FileOutputStream(output);
             JarOutputStream out = new JarOutputStream(stream, manifest))
        {
            File[] files = classesFolder.listFiles();

            if (files != null) {
                for (File clazz : files) {
                    add(classesFolder.getPath(), clazz, out);
                }
            }
        }

    }

    private static Attributes getDefAttrs() {
        Attributes attrs = new Attributes();
        attrs.put(new Attributes.Name("Created-By"), BuildConfig.APPLICATION_ID);

        return attrs;
    }

    private Manifest buildManifest(Attributes options) {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        if (options != null) {
            manifest.getMainAttributes().putAll(options);
        }
        return manifest;
    }

    public static void add(String parentPath, File source, JarOutputStream target) throws IOException {
        String name = source.getPath().substring(parentPath.length() + 1);

        BufferedInputStream in = null;

        try {
            if (source.isDirectory()) {
                if (!name.isEmpty()) {
                    if (!name.endsWith("/"))
                        name += "/";

                    JarEntry entry = new JarEntry(name);
                    entry.setTime(source.lastModified());
                    target.putNextEntry(entry);
                    target.closeEntry();
                }

                for (File nestedFile : source.listFiles()) {
                    add(parentPath, nestedFile, target);
                }
                return;
            }

            JarEntry entry = new JarEntry(name);
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(source));
            byte[] buffer = new byte[1024];
            while (true) {
                int count = in.read(buffer);
                if (count == -1)
                    break;
                target.write(buffer, 0, count);
            }
            target.closeEntry();

        } finally {
            if (in != null) {
                in.close();
            }
        }

    }
}
