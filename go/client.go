// Package webpconverter provides a Go client for the WebP Converter API.
//
// For more information, visit: https://apiverve.com/marketplace/webpconverter?utm_source=go&utm_medium=readme
package webpconverter

import (
	"errors"
	"fmt"
	"io"
	"mime/multipart"
	"os"
	"path/filepath"
	"time"

	"github.com/go-resty/resty/v2"
)

const (
	baseURL        = "https://api.apiverve.com/v1/webpconverter"
	defaultTimeout = 30 * time.Second
)

// Client is the WebP Converter API client.
type Client struct {
	apiKey     string
	httpClient *resty.Client
}

// NewClient creates a new WebP Converter API client.
func NewClient(apiKey string) *Client {
	client := resty.New()
	client.SetTimeout(defaultTimeout)
	client.SetHeader("Content-Type", "application/json")

	return &Client{
		apiKey:     apiKey,
		httpClient: client,
	}
}

// SetTimeout sets the HTTP client timeout.
func (c *Client) SetTimeout(timeout time.Duration) {
	c.httpClient.SetTimeout(timeout)
}

// ExecuteWithFile makes a request to the WebP Converter API with a file upload.
// filePath is the path to the file to upload.
// Accepted file types: .webp, .png, .jpg, .jpeg, .gif
// Max file size: 10MB
func (c *Client) ExecuteWithFile(filePath string, params map[string]string) (*Response, error) {
	if c.apiKey == "" {
		return nil, errors.New("API key is required. Get your API key at: https://apiverve.com")
	}

	req := c.httpClient.R().
		SetHeader("x-api-key", c.apiKey).
		SetResult(&Response{}).
		SetError(&ErrorResponse{}).
		SetFile("image", filePath)

	if params != nil {
		req.SetFormData(params)
	}

	resp, err := req.Post(baseURL)
	if err != nil {
		return nil, fmt.Errorf("request failed: %w", err)
	}

	if resp.IsError() {
		if errResp, ok := resp.Error().(*ErrorResponse); ok {
			return nil, fmt.Errorf("API error: %s", errResp.Error)
		}
		return nil, fmt.Errorf("API error: status %d", resp.StatusCode())
	}

	result, ok := resp.Result().(*Response)
	if !ok {
		return nil, errors.New("failed to parse response")
	}

	return result, nil
}

// Execute makes a request to the WebP Converter API with typed parameters.
//
// Parameters are validated before sending the request. If validation fails,
// an error is returned immediately without making a network request.
//
// Available parameters:
//   - image (required): string - Upload an image file to convert (supported formats: WebP, PNG, JPG, GIF)
//   - outputFormat (required): string - Target format
//   - quality: integer - Output quality (applies to jpg/webp) [min: 1, max: 100]
//   - maxWidth: integer - Maximum width in pixels (maintains aspect ratio) [min: 1, max: 10000]
//   - maxHeight: integer - Maximum height in pixels (maintains aspect ratio) [min: 1, max: 10000]
func (c *Client) Execute(req *Request) (*Response, error) {
	if c.apiKey == "" {
		return nil, errors.New("API key is required. Get your API key at: https://apiverve.com")
	}

	// Validate parameters before making request
	if req != nil {
		if err := req.Validate(); err != nil {
			return nil, err
		}
	}

	request := c.httpClient.R().
		SetHeader("x-api-key", c.apiKey).
		SetResult(&Response{}).
		SetError(&ErrorResponse{})

	// POST request with JSON body
	resp, err := request.
		SetBody(req).
		Post(baseURL)

	if err != nil {
		return nil, fmt.Errorf("request failed: %w", err)
	}

	if resp.IsError() {
		if errResp, ok := resp.Error().(*ErrorResponse); ok {
			return nil, fmt.Errorf("API error: %s", errResp.Error)
		}
		return nil, fmt.Errorf("API error: status %d", resp.StatusCode())
	}

	result, ok := resp.Result().(*Response)
	if !ok {
		return nil, errors.New("failed to parse response")
	}

	return result, nil
}

// ExecuteRaw makes a request with a raw map of parameters (for dynamic usage).
func (c *Client) ExecuteRaw(params map[string]interface{}) (*Response, error) {
	if c.apiKey == "" {
		return nil, errors.New("API key is required. Get your API key at: https://apiverve.com")
	}

	request := c.httpClient.R().
		SetHeader("x-api-key", c.apiKey).
		SetResult(&Response{}).
		SetError(&ErrorResponse{})

	resp, err := request.
		SetBody(params).
		Post(baseURL)

	if err != nil {
		return nil, fmt.Errorf("request failed: %w", err)
	}

	if resp.IsError() {
		if errResp, ok := resp.Error().(*ErrorResponse); ok {
			return nil, fmt.Errorf("API error: %s", errResp.Error)
		}
		return nil, fmt.Errorf("API error: status %d", resp.StatusCode())
	}

	result, ok := resp.Result().(*Response)
	if !ok {
		return nil, errors.New("failed to parse response")
	}

	return result, nil
}

// Response represents the API response.
type Response struct {
	Status string       `json:"status"`
	Error  interface{}  `json:"error"`
	Data   ResponseData `json:"data"`
}

// ErrorResponse represents an error response from the API.
type ErrorResponse struct {
	Status string `json:"status"`
	Error  string `json:"error"`
}
