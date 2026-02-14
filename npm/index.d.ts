declare module '@apiverve/webpconverter' {
  export interface webpconverterOptions {
    api_key: string;
    secure?: boolean;
  }

  export interface webpconverterResponse {
    status: string;
    error: string | null;
    data: WebPConverterData;
    code?: number;
  }


  interface WebPConverterData {
      id:           string;
      inputFormat:  string;
      outputFormat: string;
      inputSize:    number;
      outputSize:   number;
      mimeType:     string;
      expires:      number;
      downloadURL:  string;
  }

  export default class webpconverterWrapper {
    constructor(options: webpconverterOptions);

    execute(callback: (error: any, data: webpconverterResponse | null) => void): Promise<webpconverterResponse>;
    execute(query: Record<string, any>, callback: (error: any, data: webpconverterResponse | null) => void): Promise<webpconverterResponse>;
    execute(query?: Record<string, any>): Promise<webpconverterResponse>;
  }
}
