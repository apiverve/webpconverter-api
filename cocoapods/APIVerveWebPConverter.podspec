Pod::Spec.new do |s|
  s.name             = 'APIVerveWebPConverter'
  s.version          = '1.2.3'
  s.summary          = 'WebP Converter transforms WebP images to classic formats like PNG and JPG, or converts other formats to WebP for better compression.'
  s.description      = <<-'DESC'
WebP Converter transforms WebP images to classic formats like PNG and JPG, or converts other formats to WebP for better compression. Essential for handling modern web images.

This iOS/macOS SDK provides a convenient way to access the WebP Converter from APIVerve.
Supports both callback-based and async/await patterns.
                       DESC

  s.homepage         = 'https://apiverve.com/marketplace/webpconverter?utm_source=cocoapods&utm_medium=referral&utm_content=homepage'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'APIVerve' => 'hello@apiverve.com' }
  # The SDK repo holds every language; the pod lives under cocoapods/ with its own tag.
  s.source           = { :git => 'https://github.com/apiverve/webpconverter-api.git', :tag => "cocoapods/v#{s.version}" }

  s.ios.deployment_target = '15.0'
  s.osx.deployment_target = '12.0'

  s.swift_version = '5.0'

  s.source_files = 'cocoapods/Sources/APIVerveWebPConverter/**/*.swift'

  s.frameworks = 'Foundation'

  # APIResponse, APIVerveError and the request plumbing, shared by every APIVerve pod.
  s.dependency 'APIVerveCore', '~> 1.2'
end
