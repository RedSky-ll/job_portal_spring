// ir/parsakav/jobportal/core/storage/FileStorageService.java
package ir.parsakav.jobportal.core.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    /**
     * مسیر ریشهٔ ذخیره‌سازی روی دیسک.
     * مثال: uploads   یا   /var/data/sjp/uploads
     */
    @Value("${app.uploads.root:uploads}")
    private String uploadsRoot;

    /** مسیر فیزیکی ریشه را برگردان (abs) */
    private Path rootPath() {
        Path p = Paths.get(uploadsRoot);
        if (!p.isAbsolute()) {
            p = Paths.get(System.getProperty("user.dir")).resolve(p);
        }
        return p.normalize().toAbsolutePath();
    }

    /**
     * ذخیره فایل و برگرداندن مسیر وب برای لینک دادن
     * مثال خروجی: /uploads/resumes/uuid_filename.pdf
     */
    public String save(MultipartFile file, String subdir) throws IOException {
        String cleanName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String filename = UUID.randomUUID() + "_" + cleanName;

        Path dir = rootPath().resolve(subdir);
        Files.createDirectories(dir);

        Path dst = dir.resolve(filename).normalize();
        try (var in = file.getInputStream()) {
            Files.copy(in, dst, StandardCopyOption.REPLACE_EXISTING);
        }

        // مسیر وب (همیشه با / شروع شود)
        return "/uploads/" + subdir + "/" + filename;
    }

    /** تبدیل مسیر وب (/uploads/...) به مسیر فیزیکی روی دیسک */
    public Path resolvePhysical(String webPath) {
        if (webPath == null) throw new IllegalArgumentException("webPath is null");
        String withoutPrefix = webPath.replaceFirst("^/uploads/?", "");
        return rootPath().resolve(withoutPrefix).normalize();
    }

    /** لود فایل برای دانلود/نمایش */
    public Resource loadAsResource(String webPath) {
        Path p = resolvePhysical(webPath);
        if (!Files.exists(p)) {
            return null;
        }
        return new FileSystemResource(p.toFile());
    }

    public String filename(String webPath) {
        return resolvePhysical(webPath).getFileName().toString();
    }
}
