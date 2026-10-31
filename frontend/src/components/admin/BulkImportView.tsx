import React, { useState, useRef } from 'react';
import { BulkImportPreviewResponse, BulkImportResultResponse, ProductImportRow } from '../../types';
import { bulkImportService } from '../../services/bulkImportService';
import { useToast } from '../../context/ToastContext';
import { Button } from '../common/Button';
import { Badge } from '../common/Badge';
import {
  Upload,
  FileSpreadsheet,
  Download,
  CheckCircle2,
  AlertTriangle,
  XCircle,
  FileText,
  Layers,
  ArrowRight,
} from 'lucide-react';

interface BulkImportViewProps {
  onImportComplete: () => void;
  onNavigateToProducts: () => void;
}

export const BulkImportView: React.FC<BulkImportViewProps> = ({
  onImportComplete,
  onNavigateToProducts,
}) => {
  const { showToast } = useToast();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [isDragging, setIsDragging] = useState(false);
  const [isPreviewing, setIsPreviewing] = useState(false);
  const [isImporting, setIsImporting] = useState(false);

  const [preview, setPreview] = useState<BulkImportPreviewResponse | null>(null);
  const [filterMode, setFilterMode] = useState<'all' | 'valid' | 'errors'>('all');
  const [result, setResult] = useState<BulkImportResultResponse | null>(null);

  // Template Downloads
  const handleDownloadCsv = async () => {
    try {
      await bulkImportService.downloadCsvTemplate();
      showToast('CSV template downloaded.', 'success');
    } catch (err: any) {
      showToast(err.message || 'Failed to download CSV template.', 'error');
    }
  };

  const handleDownloadExcel = async () => {
    try {
      await bulkImportService.downloadExcelTemplate();
      showToast('Excel template downloaded.', 'success');
    } catch (err: any) {
      showToast(err.message || 'Failed to download Excel template.', 'error');
    }
  };

  // Drag and Drop
  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFileSelected(e.dataTransfer.files[0]);
    }
  };

  const handleFileSelected = (file: File) => {
    const validExtensions = ['.csv', '.xlsx', '.xls'];
    const lowerName = file.name.toLowerCase();
    const hasValidExt = validExtensions.some((ext) => lowerName.endsWith(ext));

    if (!hasValidExt) {
      showToast('Please select a valid CSV or Excel (.xlsx) file.', 'error');
      return;
    }

    if (file.size > 20 * 1024 * 1024) {
      showToast('File size exceeds the 20MB limit.', 'error');
      return;
    }

    setSelectedFile(file);
    setPreview(null);
    setResult(null);
  };

  // Validation Preview
  const handleValidatePreview = async () => {
    if (!selectedFile) return;
    setIsPreviewing(true);
    try {
      const data = await bulkImportService.previewImport(selectedFile);
      setPreview(data);
      if (data.hasErrors) {
        showToast(
          `Validation finished: ${data.validRows} valid rows, ${data.invalidRows} rows require attention.`,
          'info'
        );
      } else {
        showToast(`All ${data.validRows} products passed schema validation!`, 'success');
      }
    } catch (err: any) {
      showToast(err.message || 'Validation failed. Please check file formatting.', 'error');
    } finally {
      setIsPreviewing(false);
    }
  };

  // Download Error Report as CSV
  const handleDownloadErrorReport = () => {
    if (!preview || preview.invalidRows === 0) return;

    const invalidRows = preview.rows.filter((r) => !r.valid);
    const headers = ['Row Number', 'SKU', 'Name', 'Category', 'Price', 'Errors'];
    const csvLines = [headers.join(',')];

    invalidRows.forEach((r) => {
      const escapedName = `"${(r.name || '').replace(/"/g, '""')}"`;
      const escapedCategory = `"${(r.categoryName || '').replace(/"/g, '""')}"`;
      const escapedErrors = `"${r.errors.join('; ').replace(/"/g, '""')}"`;
      csvLines.push(
        [r.rowNumber, r.sku || '', escapedName, escapedCategory, r.price ?? '', escapedErrors].join(',')
      );
    });

    const csvContent = csvLines.join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `import_error_report_${Date.now()}.csv`);
    document.body.appendChild(link);
    link.click();
    link.parentNode?.removeChild(link);
    window.URL.revokeObjectURL(url);
    showToast('Diagnostic error report generated.', 'success');
  };

  // Execute Confirmed Import
  const handleExecuteImport = async () => {
    if (!selectedFile || !preview) return;

    if (preview.validRows === 0) {
      showToast('No valid rows found in file to import.', 'error');
      return;
    }

    const confirmMsg = preview.invalidRows > 0
      ? `Proceed with importing ${preview.validRows} valid products? ${preview.invalidRows} invalid rows will be skipped.`
      : `Ready to commit and publish ${preview.validRows} products to the catalog?`;

    if (!window.confirm(confirmMsg)) return;

    setIsImporting(true);
    try {
      const data = await bulkImportService.executeImport(selectedFile);
      setResult(data);
      showToast(data.summaryMessage, 'success');
      onImportComplete();
    } catch (err: any) {
      showToast(err.message || 'Bulk import execution failed.', 'error');
    } finally {
      setIsImporting(false);
    }
  };

  const handleReset = () => {
    setSelectedFile(null);
    setPreview(null);
    setResult(null);
    if (fileInputRef.current) fileInputRef.current.value = '';
  };

  const filteredPreviewRows = preview?.rows.filter((r) => {
    if (filterMode === 'valid') return r.valid;
    if (filterMode === 'errors') return !r.valid;
    return true;
  });

  return (
    <div className="space-y-6">
      {/* 1. Template Download & Instructions Header */}
      <div className="bg-luxury-surface border border-luxury-border p-6 rounded-sm space-y-4">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-4 border-b border-luxury-border">
          <div>
            <h3 className="font-serif font-bold text-lg text-luxury-white flex items-center gap-2">
              <FileSpreadsheet size={20} className="text-luxury-gold" />
              Catalogue Bulk Import Engine
            </h3>
            <p className="text-xs text-luxury-muted mt-1">
              Import hundreds of products simultaneously via standardized CSV (RFC 4180) or Excel (.xlsx) workbooks.
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <Button
              variant="dark"
              size="sm"
              onClick={handleDownloadCsv}
              leftIcon={<Download size={14} />}
            >
              Download CSV Template
            </Button>
            <Button
              variant="gold"
              size="sm"
              onClick={handleDownloadExcel}
              leftIcon={<Download size={14} />}
            >
              Download Excel Template
            </Button>
          </div>
        </div>

        {/* Workflow Steps Indicator */}
        <div className="grid grid-cols-1 sm:grid-cols-4 gap-3 text-xs">
          <div className="p-3 bg-luxury-elevated/40 border border-luxury-border/60 rounded-sm">
            <span className="font-bold text-luxury-gold block mb-0.5">Step 1. Prepare File</span>
            <span className="text-[11px] text-luxury-muted">Use standard columns with matching department names.</span>
          </div>
          <div className="p-3 bg-luxury-elevated/40 border border-luxury-border/60 rounded-sm">
            <span className="font-bold text-luxury-gold block mb-0.5">Step 2. Upload &amp; Preview</span>
            <span className="text-[11px] text-luxury-muted">Inspect schema validation without database modification.</span>
          </div>
          <div className="p-3 bg-luxury-elevated/40 border border-luxury-border/60 rounded-sm">
            <span className="font-bold text-luxury-gold block mb-0.5">Step 3. Resolve Diagnostics</span>
            <span className="text-[11px] text-luxury-muted">Export error report to correct invalid records offline.</span>
          </div>
          <div className="p-3 bg-luxury-elevated/40 border border-luxury-border/60 rounded-sm">
            <span className="font-bold text-luxury-gold block mb-0.5">Step 4. Safe Ingestion</span>
            <span className="text-[11px] text-luxury-muted">Atomic transactional commit with cache invalidation.</span>
          </div>
        </div>
      </div>

      {/* 2. Success Result Banner (when execution finished) */}
      {result && (
        <div className="bg-emerald-950/60 border border-emerald-500/50 p-6 rounded-sm space-y-4">
          <div className="flex items-start gap-3">
            <CheckCircle2 size={24} className="text-emerald-400 shrink-0 mt-0.5" />
            <div className="flex-1">
              <h4 className="font-serif font-bold text-base text-luxury-white">
                Bulk Import Executed Successfully
              </h4>
              <p className="text-xs text-luxury-muted mt-1">{result.summaryMessage}</p>
            </div>
          </div>

          <div className="flex flex-wrap items-center gap-3 pt-2">
            <Button
              variant="gold"
              size="sm"
              onClick={onNavigateToProducts}
              rightIcon={<ArrowRight size={14} />}
            >
              View Updated Catalog
            </Button>
            <Button variant="ghost" size="sm" onClick={handleReset}>
              Import Another File
            </Button>
          </div>
        </div>
      )}

      {/* 3. Drag and Drop Upload Zone */}
      {!result && (
        <div className="bg-luxury-surface border border-luxury-border p-6 rounded-sm space-y-4">
          <div
            onDragOver={handleDragOver}
            onDragLeave={handleDragLeave}
            onDrop={handleDrop}
            onClick={() => fileInputRef.current?.click()}
            className={`border-2 border-dashed rounded-sm p-8 text-center cursor-pointer transition-colors ${
              isDragging
                ? 'border-luxury-gold bg-luxury-gold/5'
                : 'border-luxury-border hover:border-luxury-gold/50 bg-luxury-elevated/20'
            }`}
          >
            <input
              ref={fileInputRef}
              type="file"
              accept=".csv, .xlsx, .xls, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, text/csv"
              className="hidden"
              onChange={(e) => {
                if (e.target.files && e.target.files[0]) {
                  handleFileSelected(e.target.files[0]);
                }
              }}
            />
            <Upload size={32} className="mx-auto text-luxury-gold mb-2" />
            <h4 className="font-serif font-bold text-sm text-luxury-white">
              {selectedFile ? selectedFile.name : 'Select or drag catalog file here'}
            </h4>
            <p className="text-xs text-luxury-muted mt-1">
              {selectedFile
                ? `${(selectedFile.size / 1024).toFixed(1)} KB – Click to replace`
                : 'Supports CSV (RFC 4180) and Excel (.xlsx) files up to 20MB'}
            </p>
          </div>

          {selectedFile && (
            <div className="flex flex-col sm:flex-row items-center justify-between gap-3 pt-2">
              <div className="flex items-center gap-2 text-xs text-luxury-muted">
                <FileText size={16} className="text-luxury-gold" />
                <span className="text-luxury-white font-medium">{selectedFile.name}</span>
                <span>({(selectedFile.size / 1024).toFixed(1)} KB)</span>
              </div>

              <div className="flex items-center gap-2">
                <Button variant="ghost" size="sm" onClick={handleReset} disabled={isPreviewing || isImporting}>
                  Clear
                </Button>
                <Button
                  variant="gold"
                  size="sm"
                  onClick={handleValidatePreview}
                  disabled={isPreviewing || isImporting}
                  isLoading={isPreviewing}
                  leftIcon={<Layers size={14} />}
                >
                  Inspect &amp; Validate File
                </Button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* 4. Preview & Diagnostics Dashboard */}
      {preview && !result && (
        <div className="space-y-6">
          {/* Diagnostic KPI Cards */}
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <div className="bg-luxury-surface border border-luxury-border p-4 rounded-sm">
              <span className="text-xs text-luxury-muted uppercase font-bold tracking-wider block mb-1">
                Total File Rows
              </span>
              <div className="font-serif text-2xl font-bold text-luxury-white">
                {preview.totalRows}
              </div>
              <span className="text-[11px] text-luxury-muted mt-1 block">Parsed data rows</span>
            </div>

            <div className="bg-luxury-surface border border-luxury-border p-4 rounded-sm">
              <span className="text-xs text-emerald-400 uppercase font-bold tracking-wider block mb-1">
                Valid Records
              </span>
              <div className="font-serif text-2xl font-bold text-emerald-400">
                {preview.validRows}
              </div>
              <span className="text-[11px] text-emerald-400/80 mt-1 block">Ready for ingestion</span>
            </div>

            <div className="bg-luxury-surface border border-luxury-border p-4 rounded-sm">
              <span className="text-xs text-rose-400 uppercase font-bold tracking-wider block mb-1">
                Invalid Records
              </span>
              <div className="font-serif text-2xl font-bold text-rose-400">
                {preview.invalidRows}
              </div>
              <span className="text-[11px] text-rose-400/80 mt-1 block">Contain schema violations</span>
            </div>

            <div className="bg-luxury-surface border border-luxury-border p-4 rounded-sm">
              <span className="text-xs text-amber-400 uppercase font-bold tracking-wider block mb-1">
                SKU Collisions
              </span>
              <div className="font-serif text-2xl font-bold text-amber-400">
                {preview.duplicateSkusCount}
              </div>
              <span className="text-[11px] text-amber-400/80 mt-1 block">Duplicate or existing SKUs</span>
            </div>
          </div>

          {/* Diagnostic Actions & Filters Bar */}
          <div className="bg-luxury-surface border border-luxury-border p-4 rounded-sm flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div className="flex items-center gap-2">
              <button
                type="button"
                onClick={() => setFilterMode('all')}
                className={`px-3 py-1.5 rounded-sm text-xs font-medium transition-colors ${
                  filterMode === 'all'
                    ? 'bg-luxury-elevated text-luxury-gold border border-luxury-gold/40'
                    : 'text-luxury-muted hover:text-luxury-white'
                }`}
              >
                All Rows ({preview.totalRows})
              </button>
              <button
                type="button"
                onClick={() => setFilterMode('valid')}
                className={`px-3 py-1.5 rounded-sm text-xs font-medium transition-colors ${
                  filterMode === 'valid'
                    ? 'bg-emerald-950/60 text-emerald-400 border border-emerald-500/40'
                    : 'text-luxury-muted hover:text-luxury-white'
                }`}
              >
                Valid Only ({preview.validRows})
              </button>
              <button
                type="button"
                onClick={() => setFilterMode('errors')}
                className={`px-3 py-1.5 rounded-sm text-xs font-medium transition-colors ${
                  filterMode === 'errors'
                    ? 'bg-rose-950/60 text-rose-400 border border-rose-500/40'
                    : 'text-luxury-muted hover:text-luxury-white'
                }`}
              >
                Errors Only ({preview.invalidRows})
              </button>
            </div>

            {preview.invalidRows > 0 && (
              <Button
                variant="dark"
                size="sm"
                onClick={handleDownloadErrorReport}
                leftIcon={<Download size={13} />}
              >
                Download Error Report (.CSV)
              </Button>
            )}
          </div>

          {/* Diagnostic Table */}
          <div className="bg-luxury-surface border border-luxury-border rounded-sm overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-luxury-elevated/60 text-luxury-gold uppercase tracking-wider border-b border-luxury-border">
                <tr>
                  <th className="p-3 w-14">Row</th>
                  <th className="p-3 w-24">Validation</th>
                  <th className="p-3">Artifact</th>
                  <th className="p-3">Category</th>
                  <th className="p-3">SKU</th>
                  <th className="p-3">Price</th>
                  <th className="p-3">Stock</th>
                  <th className="p-3">Diagnostic Remarks</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-luxury-border">
                {filteredPreviewRows && filteredPreviewRows.length > 0 ? (
                  filteredPreviewRows.map((row: ProductImportRow) => (
                    <tr
                      key={row.rowNumber}
                      className={row.valid ? 'hover:bg-luxury-elevated/20' : 'bg-rose-950/10 hover:bg-rose-950/20'}
                    >
                      <td className="p-3 font-mono text-luxury-muted">{row.rowNumber}</td>
                      <td className="p-3">
                        {row.valid ? (
                          <Badge variant="dark" size="sm" className="border-emerald-500/40 text-emerald-400">
                            Valid
                          </Badge>
                        ) : (
                          <Badge variant="danger" size="sm">
                            Error ({row.errors.length})
                          </Badge>
                        )}
                      </td>
                      <td className="p-3 font-serif font-bold text-luxury-white">
                        {row.name || <span className="text-luxury-subtle italic">Missing name</span>}
                        {row.brand && (
                          <span className="text-[10px] text-luxury-muted font-sans block">{row.brand}</span>
                        )}
                      </td>
                      <td className="p-3 text-luxury-muted">{row.categoryName || '–'}</td>
                      <td className="p-3 font-mono text-luxury-muted">{row.sku || '–'}</td>
                      <td className="p-3 font-bold text-luxury-white">
                        {row.price !== undefined && row.price !== null ? `$${Number(row.price).toFixed(2)}` : '–'}
                      </td>
                      <td className="p-3 text-luxury-muted">{row.stockQuantity ?? '–'}</td>
                      <td className="p-3">
                        {row.valid ? (
                          <span className="text-emerald-400 text-[11px] flex items-center gap-1">
                            <CheckCircle2 size={12} /> Ready
                          </span>
                        ) : (
                          <div className="space-y-1">
                            {row.errors.map((err, i) => (
                              <span
                                key={i}
                                className="text-rose-400 text-[11px] flex items-center gap-1"
                              >
                                <XCircle size={12} className="shrink-0" />
                                {err}
                              </span>
                            ))}
                          </div>
                        )}
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={8} className="p-8 text-center text-luxury-muted">
                      No records match the active filter.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          {/* Confirmation Execution Bar */}
          <div className="bg-luxury-surface border border-luxury-border p-6 rounded-sm flex flex-col sm:flex-row items-center justify-between gap-4">
            <div>
              <h4 className="font-serif font-bold text-sm text-luxury-white">
                Execute Catalog Import
              </h4>
              <p className="text-xs text-luxury-muted mt-0.5">
                {preview.invalidRows > 0 ? (
                  <span className="text-amber-400 flex items-center gap-1 font-medium mt-1">
                    <AlertTriangle size={13} />
                    {preview.invalidRows} invalid rows will be skipped. {preview.validRows} valid products will be ingested.
                  </span>
                ) : (
                  <span>All {preview.validRows} products will be safely ingested into catalog.</span>
                )}
              </p>
            </div>

            <div className="flex items-center gap-3">
              <Button variant="ghost" onClick={handleReset} disabled={isImporting}>
                Cancel
              </Button>
              <Button
                variant="gold"
                onClick={handleExecuteImport}
                disabled={preview.validRows === 0 || isImporting}
                isLoading={isImporting}
                leftIcon={<Upload size={14} />}
              >
                Confirm &amp; Import {preview.validRows} Products
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
