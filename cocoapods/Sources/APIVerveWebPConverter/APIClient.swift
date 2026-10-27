@_exported import APIVerveCore
import Foundation

/// Main API client for WebP Converter
public class WebPConverterAPIClient {
    private let transport: APIVerveTransport

    /// Initialize the API client
    /// - Parameter apiKey: Your APIVerve API key from https://apiverve.com
    public init(apiKey: String) {
        self.transport = APIVerveTransport(apiKey: apiKey, apiID: "webpconverter", authMode: "cocoapods-package")
    }

    /// Execute the API request
    /// - Parameters:
    ///   - parameters: Query parameters or request body
    ///   - completion: Completion handler with Result type
    public func execute(parameters: [String: Any]? = nil, completion: @escaping (Result<APIResponse, WebPConverterAPIError>) -> Void) {
        transport.post(parameters: parameters, completion: completion)
    }
}

// MARK: - Async/Await Support (iOS 13+)
@available(iOS 13.0, macOS 10.15, tvOS 13.0, watchOS 6.0, *)
extension WebPConverterAPIClient {
    /// Execute the API request using async/await
    /// - Parameter parameters: Query parameters or request body
    /// - Returns: APIResponse on success
    /// - Throws: WebPConverterAPIError on failure
    public func execute(parameters: [String: Any]? = nil) async throws -> APIResponse {
        try await withCheckedThrowingContinuation { continuation in
            execute(parameters: parameters) { result in
                continuation.resume(with: result)
            }
        }
    }

    /// Execute file upload using async/await
    public func executeWithFile(fileData: Data, fileName: String, fieldName: String = "image", mimeType: String = "image/jpeg") async throws -> APIResponse {
        try await withCheckedThrowingContinuation { continuation in
            executeWithFile(fileData: fileData, fileName: fileName, fieldName: fieldName, mimeType: mimeType) { result in
                continuation.resume(with: result)
            }
        }
    }
}

// MARK: - File Upload Support
extension WebPConverterAPIClient {
    /// Execute the API request with a file upload
    /// - Parameters:
    ///   - fileData: The file data to upload
    ///   - fileName: Name of the file
    ///   - fieldName: Form field name for the file (default: "image")
    ///   - mimeType: MIME type of the file (default: "image/jpeg")
    ///   - completion: Completion handler with Result type
    public func executeWithFile(fileData: Data, fileName: String, fieldName: String = "image", mimeType: String = "image/jpeg", completion: @escaping (Result<APIResponse, WebPConverterAPIError>) -> Void) {
        transport.upload(fileData: fileData, fileName: fileName, fieldName: fieldName, mimeType: mimeType, completion: completion)
    }
}
