# WebP Converter API - PHP Package

WebP Converter transforms WebP images to classic formats like PNG and JPG, or converts other formats to WebP for better compression. Essential for handling modern web images.

## Installation

Install via Composer:

```bash
composer require apiverve/webpconverter
```

## Getting Started

Get your API key at [APIVerve](https://apiverve.com)

### Basic Usage

```php
<?php

require_once 'vendor/autoload.php';

use APIVerve\Webpconverter\Client;

// Initialize the client
$client = new Client('YOUR_API_KEY');

// Make a request
$response = $client->execute([
    'image' => '',
    'outputFormat' => 'png',
    'quality' => 90,
    'maxWidth' => 1920,
    'maxHeight' => 1080
]);

// Print the response
print_r($response);
```

### File Upload

```php
// Upload a file
$response = $client->executeWithFile('/path/to/file.jpg');

// Or use a URL
$response = $client->executeWithUrl('https://example.com/image.jpg');
```

### Error Handling

```php
use APIVerve\Webpconverter\Client;
use APIVerve\Webpconverter\Exceptions\APIException;
use APIVerve\Webpconverter\Exceptions\ValidationException;

try {
    $response = $client->execute(['outputFormat' => 'png']);
    print_r($response['data']);
} catch (ValidationException $e) {
    echo "Validation error: " . implode(', ', $e->getErrors());
} catch (APIException $e) {
    echo "API error: " . $e->getMessage();
    echo "Status code: " . $e->getStatusCode();
}
```

### Debug Mode

```php
// Enable debug logging
$client = new Client(
    apiKey: 'YOUR_API_KEY',
    debug: true
);
```

## Example Response

```json
{
  "status": "ok",
  "error": null,
  "data": {
    "id": "c9d8e7f6-5432-10ba-dcfe-210987654321",
    "inputFormat": "webp",
    "outputFormat": "png",
    "inputSize": 156789,
    "outputSize": 234567,
    "mimeType": "image/png",
    "expires": 1707350400000,
    "downloadURL": "https://storage.googleapis.com/apiverve/webpconverter/c9d8e7f6.png"
  },
  "code": 200
}
```

## Requirements

- PHP 7.4 or higher
- Guzzle HTTP client

## Documentation

For more information, visit the [API Documentation](https://docs.apiverve.com/ref/webpconverter?utm_source=packagist&utm_medium=referral&utm_content=readme).

## Support

- Website: [https://apiverve.com/marketplace/webpconverter?utm_source=php&utm_medium=referral&utm_content=readme](https://apiverve.com/marketplace/webpconverter?utm_source=php&utm_medium=referral&utm_content=readme)
- Email: hello@apiverve.com

## License

This package is available under the [MIT License](LICENSE).
