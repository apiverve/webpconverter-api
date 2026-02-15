using System;
using System.Collections.Generic;
using System.Text;
using Newtonsoft.Json;

namespace APIVerve.API.WebPConverter
{
    /// <summary>
    /// Query options for the WebP Converter API
    /// </summary>
    public class WebPConverterQueryOptions
    {
        /// <summary>
        /// Upload an image file to convert (supported formats: WebP, PNG, JPG, GIF)
        /// </summary>
        [JsonProperty("image")]
        public string Image { get; set; }

        /// <summary>
        /// Target format
        /// </summary>
        [JsonProperty("outputFormat")]
        public string OutputFormat { get; set; }

        /// <summary>
        /// Output quality (applies to jpg/webp)
        /// </summary>
        [JsonProperty("quality")]
        public string Quality { get; set; }

        /// <summary>
        /// Maximum width in pixels (maintains aspect ratio)
        /// </summary>
        [JsonProperty("maxWidth")]
        public string MaxWidth { get; set; }

        /// <summary>
        /// Maximum height in pixels (maintains aspect ratio)
        /// </summary>
        [JsonProperty("maxHeight")]
        public string MaxHeight { get; set; }
    }
}
