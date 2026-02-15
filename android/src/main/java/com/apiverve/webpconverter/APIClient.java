package com.apiverve.webpconverter;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Validation rule for a parameter.
 */
class ValidationRule {
    private String type = "string";
    private boolean required = false;
    private Double min;
    private Double max;
    private Integer minLength;
    private Integer maxLength;
    private String format;
    private List<String> enumValues;

    public ValidationRule setType(String type) { this.type = type; return this; }
    public ValidationRule setRequired(boolean required) { this.required = required; return this; }
    public ValidationRule setMin(double min) { this.min = min; return this; }
    public ValidationRule setMax(double max) { this.max = max; return this; }
    public ValidationRule setMinLength(int minLength) { this.minLength = minLength; return this; }
    public ValidationRule setMaxLength(int maxLength) { this.maxLength = maxLength; return this; }
    public ValidationRule setFormat(String format) { this.format = format; return this; }
    public ValidationRule setEnumValues(List<String> enumValues) { this.enumValues = enumValues; return this; }

    public String getType() { return type; }
    public boolean isRequired() { return required; }
    public Double getMin() { return min; }
    public Double getMax() { return max; }
    public Integer getMinLength() { return minLength; }
    public Integer getMaxLength() { return maxLength; }
    public String getFormat() { return format; }
    public List<String> getEnumValues() { return enumValues; }
}

/**
 * Exception thrown when parameter validation fails.
 */
class ValidationException extends APIException {
    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super("Validation failed: " + String.join("; ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() { return errors; }
}

/**
 * Main API client for WebP Converter
 * Provides a simple interface to access the APIVerve WebP Converter
 *
 * <p>Parameters:</p>
 * <ul>

 * </ul>
 */
public class WebPConverterAPIClient {
    private final String apiKey;
    private final String baseURL;
    private static final int CONNECT_TIMEOUT = 30000; // 30 seconds
    private static final int READ_TIMEOUT = 30000; // 30 seconds

    /** Validation rules for parameters */
    private static final Map<String, ValidationRule> VALIDATION_RULES = createValidationRules();

    /** Format validation patterns */
    private static final Map<String, Pattern> FORMAT_PATTERNS = createFormatPatterns();

    private static Map<String, ValidationRule> createValidationRules() {
        Map<String, ValidationRule> rules = new HashMap<>();
        rules.put("image", new ValidationRule().setType("string").setRequired(true));
        rules.put("outputFormat", new ValidationRule().setType("string").setRequired(true));
        rules.put("quality", new ValidationRule().setType("integer").setRequired(false).setMin(1).setMax(100));
        rules.put("maxWidth", new ValidationRule().setType("integer").setRequired(false).setMin(1).setMax(10000));
        rules.put("maxHeight", new ValidationRule().setType("integer").setRequired(false).setMin(1).setMax(10000));
        return rules;
    }

    private static Map<String, Pattern> createFormatPatterns() {
        Map<String, Pattern> patterns = new HashMap<>();
        patterns.put("email", Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"));
        patterns.put("url", Pattern.compile("^https?://.+"));
        patterns.put("ip", Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$"));
        patterns.put("date", Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$"));
        patterns.put("hexColor", Pattern.compile("^#?([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$"));
        return patterns;
    }

    /**
     * Initialize the API client
     * @param apiKey Your APIVerve API key from https://apiverve.com
     * @throws IllegalArgumentException if API key is invalid
     */
    public WebPConverterAPIClient(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key must be provided. Get your API key at: https://apiverve.com");
        }

        // Validate API key format (alphanumeric with hyphens)
        if (!apiKey.matches("^[a-zA-Z0-9-]+$")) {
            throw new IllegalArgumentException("Invalid API key format. API key must be alphanumeric and may contain hyphens");
        }

        // Check minimum length (GUIDs are typically 36 chars with hyphens, or 32 without)
        String trimmedKey = apiKey.replace("-", "");
        if (trimmedKey.length() < 32) {
            throw new IllegalArgumentException("Invalid API key. API key appears to be too short");
        }

        this.apiKey = apiKey;
        this.baseURL = "https://api.apiverve.com/v1/webpconverter";
    }

    /**
     * Validates parameters against defined rules.
     * @param parameters The parameters to validate
     * @throws ValidationException if validation fails
     */
    private void validateParams(Map<String, Object> parameters) throws ValidationException {
        if (VALIDATION_RULES.isEmpty()) return;

        List<String> errors = new ArrayList<>();
        Map<String, Object> params = parameters != null ? parameters : new HashMap<>();

        for (Map.Entry<String, ValidationRule> entry : VALIDATION_RULES.entrySet()) {
            String paramName = entry.getKey();
            ValidationRule rule = entry.getValue();
            Object value = params.get(paramName);

            // Check required
            if (rule.isRequired() && (value == null || (value instanceof String && ((String) value).isEmpty()))) {
                errors.add("Required parameter [" + paramName + "] is missing");
                continue;
            }

            if (value == null) continue;

            // Type-specific validation
            String type = rule.getType();
            if ("integer".equals(type) || "number".equals(type)) {
                try {
                    double numValue = value instanceof Number ? ((Number) value).doubleValue() : Double.parseDouble(value.toString());
                    if (rule.getMin() != null && numValue < rule.getMin()) {
                        errors.add("Parameter [" + paramName + "] must be at least " + rule.getMin());
                    }
                    if (rule.getMax() != null && numValue > rule.getMax()) {
                        errors.add("Parameter [" + paramName + "] must be at most " + rule.getMax());
                    }
                } catch (NumberFormatException e) {
                    errors.add("Parameter [" + paramName + "] must be a valid " + type);
                }
            } else if ("string".equals(type) && value instanceof String) {
                String strValue = (String) value;
                if (rule.getMinLength() != null && strValue.length() < rule.getMinLength()) {
                    errors.add("Parameter [" + paramName + "] must be at least " + rule.getMinLength() + " characters");
                }
                if (rule.getMaxLength() != null && strValue.length() > rule.getMaxLength()) {
                    errors.add("Parameter [" + paramName + "] must be at most " + rule.getMaxLength() + " characters");
                }
                if (rule.getFormat() != null && FORMAT_PATTERNS.containsKey(rule.getFormat())) {
                    if (!FORMAT_PATTERNS.get(rule.getFormat()).matcher(strValue).matches()) {
                        errors.add("Parameter [" + paramName + "] must be a valid " + rule.getFormat());
                    }
                }
            }

            // Enum validation
            if (rule.getEnumValues() != null && !rule.getEnumValues().isEmpty()) {
                if (!rule.getEnumValues().contains(value.toString())) {
                    errors.add("Parameter [" + paramName + "] must be one of: " + String.join(", ", rule.getEnumValues()));
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    /**
     * Execute the API request
     * @param parameters Query parameters or request body (can be null)
     * @return APIResponse object containing the response
     * @throws APIException if the request fails
     * @throws ValidationException if parameter validation fails
     */
    public APIResponse execute(Map<String, Object> parameters) throws APIException {
        // Validate parameters before making request
        validateParams(parameters);

        return executeGet(parameters);
    }

    /**
     * Execute the API request without parameters
     * @return APIResponse object containing the response
     * @throws APIException if the request fails
     */
    public APIResponse execute() throws APIException {
        return execute(null);
    }

    private APIResponse executeGet(Map<String, Object> parameters) throws APIException {
        try {
            String urlString = baseURL;

            // Add query parameters
            if (parameters != null && !parameters.isEmpty()) {
                StringBuilder queryString = new StringBuilder("?");
                boolean first = true;
                for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                    if (!first) {
                        queryString.append("&");
                    }
                    queryString.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    queryString.append("=");
                    queryString.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                    first = false;
                }
                urlString += queryString.toString();
            }

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestProperty("x-api-key", apiKey);
            conn.setRequestProperty("auth-mode", "android-package");

            return handleResponse(conn);
        } catch (Exception e) {
            throw new APIException("Network error: " + e.getMessage(), e);
        }
    }

    private APIResponse handleResponse(HttpURLConnection conn) throws APIException {
        try {
            int statusCode = conn.getResponseCode();
            BufferedReader reader;

            if (statusCode == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String responseBody = response.toString();

            if (statusCode != 200) {
                throw new APIException("HTTP error " + statusCode + ": " + responseBody, statusCode);
            }

            return new APIResponse(responseBody);
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            throw new APIException("Failed to process response: " + e.getMessage(), e);
        }
    }

    /**
     * Execute the API request with a file upload
     * @param file The file to upload
     * @param fieldName The form field name for the file (e.g., "image")
     * @return APIResponse object containing the response
     * @throws APIException if the request fails
     */
    public APIResponse executeWithFile(File file, String fieldName) throws APIException {
        if (file == null || !file.exists()) {
            throw new APIException("File does not exist or is null");
        }

        String boundary = "----" + UUID.randomUUID().toString();
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        try {
            URL url = new URL(baseURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setRequestProperty("x-api-key", apiKey);
            conn.setRequestProperty("auth-mode", "android-package");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());

            // Write file part
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + file.getName() + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + getMimeType(file.getName()) + lineEnd);
            outputStream.writeBytes(lineEnd);

            // Write file data
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            fileInputStream.close();

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            outputStream.flush();
            outputStream.close();

            return handleResponse(conn);
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            throw new APIException("File upload error: " + e.getMessage(), e);
        }
    }

    /**
     * Execute the API request with a file upload using default field name
     * @param file The file to upload
     * @return APIResponse object containing the response
     * @throws APIException if the request fails
     */
    public APIResponse executeWithFile(File file) throws APIException {
        return executeWithFile(file, "image");
    }

    /**
     * Get the MIME type based on file extension
     */
    private String getMimeType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "bmp":
                return "image/bmp";
            case "pdf":
                return "application/pdf";
            default:
                return "application/octet-stream";
        }
    }
}
