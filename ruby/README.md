# WebP Converter API - Ruby Gem

WebP Converter transforms WebP images to classic formats like PNG and JPG, or converts other formats to WebP for better compression. Essential for handling modern web images.

## Installation

Add this line to your application's Gemfile:

```ruby
gem 'apiverve_webpconverter'
```

And then execute:

```bash
$ bundle install
```

Or install it yourself as:

```bash
$ gem install apiverve_webpconverter
```

## Getting Started

Get your API key at [APIVerve](https://apiverve.com)

### Basic Usage

```ruby
require 'apiverve_webpconverter'

# Initialize the client
client = APIVerve::Webpconverter::Client.new(api_key: "YOUR_API_KEY")

# Make a request
response = client.execute({
  image: "",
  outputFormat: "png",
  quality: 90,
  maxWidth: 1920,
  maxHeight: 1080
})

# Print the response
puts response
```

### File Upload

```ruby
# Upload a file
response = client.execute_with_file("/path/to/file.jpg")

# Or use a URL
response = client.execute_with_url("https://example.com/image.jpg")
```

### Error Handling

```ruby
begin
  response = client.execute({ outputFormat: "png" })
  puts response["data"]
rescue APIVerve::Webpconverter::ValidationError => e
  puts "Validation error: #{e.errors.join(', ')}"
rescue APIVerve::Webpconverter::APIError => e
  puts "API error: #{e.message}"
  puts "Status code: #{e.status_code}"
end
```

### Debug Mode

```ruby
# Enable debug logging
client = APIVerve::Webpconverter::Client.new(
  api_key: "YOUR_API_KEY",
  debug: true
)
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

## Documentation

For more information, visit the [API Documentation](https://docs.apiverve.com/ref/webpconverter?utm_source=rubygems&utm_medium=referral&utm_content=readme).

## Support

- Website: [https://apiverve.com/marketplace/webpconverter?utm_source=ruby&utm_medium=referral&utm_content=readme](https://apiverve.com/marketplace/webpconverter?utm_source=ruby&utm_medium=referral&utm_content=readme)
- Email: hello@apiverve.com

## License

This gem is available under the [MIT License](LICENSE).
