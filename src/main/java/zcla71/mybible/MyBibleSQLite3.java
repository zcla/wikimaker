package zcla71.mybible;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import zcla71.mybible.model.Database;

@Slf4j
public class MyBibleSQLite3 extends MyBible {
    public MyBibleSQLite3(URI uri, String id) throws Exception {
        super(uri, id);
    }

    @Override
    protected Database doDownload() throws Exception {
        Database result = new Database();
        result.setUrl(getUri().toString());
        log.info("URI: " + getUri().toString());
        result.setDownloadedFileName(getDownloadFileName(getUri()));
        log.info("Downloaded file name: " + result.getDownloadedFileName());
        result.setTimestamp(LocalDateTime.now());

        File downloaded = null;
        try {
            downloaded = download(result);
            sql(result, downloaded);
        } finally {
            if (downloaded != null) {
                downloaded.delete();
            }
        }

        return result;
    }

    private File download(Database database) throws IOException {
        File result = File.createTempFile(removeExtension(database.getDownloadedFileName()) + ".", ".SQLite3", getTempDirectory());
        super.download(result);
        return result;
    }

    private void sql(Database database, File downloaded) throws SQLException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException {
        sqlBible(database, downloaded.getPath());
    }
}
