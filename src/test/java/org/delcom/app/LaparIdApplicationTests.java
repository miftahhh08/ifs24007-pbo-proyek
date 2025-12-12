package org.delcom.app;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class LaparIdApplicationTests {

    @Test
    @DisplayName("Pengujian Main Class Application (100% Coverage)")
    public void testLaparIdApplication() {

        // ==========================================
        // 1. TEST CONSTRUCTOR (Untuk Class Coverage)
        // ==========================================
        {
            // Instansiasi manual untuk memastikan default constructor tereksekusi
            LaparIdApplication app = new LaparIdApplication();
            assertNotNull(app);
        }

        // ==========================================
        // 2. TEST MAIN METHOD (Tanpa Start Server)
        // ==========================================
        {
            // Mock Static SpringApplication agar tidak loading context Spring beneran
            try (MockedStatic<SpringApplication> springMock = mockStatic(SpringApplication.class)) {
                
                // Siapkan dummy context
                ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
                
                // Konfigurasi mock: saat SpringApplication.run(...) dipanggil, return dummy context
                springMock.when(() -> SpringApplication.run(eq(LaparIdApplication.class), any(String[].class)))
                          .thenReturn(context);

                // Eksekusi method main
                String[] args = new String[]{};
                LaparIdApplication.main(args);

                // Verifikasi bahwa SpringApplication.run benar-benar dipanggil
                springMock.verify(() -> SpringApplication.run(eq(LaparIdApplication.class), any(String[].class)));
            }
        }
    }
}