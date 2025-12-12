package org.delcom.app.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AppServiceTests {

    @Test
    @DisplayName("Pengujian AppService (100% Coverage)")
    public void testAppService() {
        
        // ==========================================
        // SKENARIO: INSTANSIASI (DEFAULT CONSTRUCTOR)
        // ==========================================
        {
            // Karena kelas ini kosong dan hanya punya default constructor implisit,
            // kita cukup menginstansiasinya untuk mendapatkan 100% coverage.
            AppService appService = new AppService();
            
            assertNotNull(appService, "AppService harus bisa diinstansiasi");
        }
    }
}