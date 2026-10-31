package com.jpbazaar.service;

import com.jpbazaar.dto.bulk.BulkImportPreviewResponse;
import com.jpbazaar.dto.bulk.BulkImportResultResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BulkImportService {

    byte[] generateCsvTemplate();

    byte[] generateExcelTemplate();

    BulkImportPreviewResponse previewImport(MultipartFile file);

    BulkImportPreviewResponse previewImport(java.io.InputStream inputStream, String filename);

    BulkImportResultResponse executeImport(MultipartFile file);

    BulkImportResultResponse executeImport(java.io.InputStream inputStream, String filename);
}
