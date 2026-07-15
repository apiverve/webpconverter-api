declare module '@apiverve/webpconverter' {
  export interface webpconverterOptions {
    api_key: string;
    secure?: boolean;
  }

  /**
   * Describes fields the current plan does not unlock. Locked fields arrive as null
   * in `data`; `locked_fields` names them, using dot paths for nested fields.
   * Absent when the plan unlocks everything.
   */
  export interface PremiumInfo {
    message: string;
    upgrade_url: string;
    locked_fields: string[];
  }

  export interface webpconverterResponse {
    status: string;
    error: string | null;
    data: WebPConverterData;
    code?: number;
    premium?: PremiumInfo;
  }


  interface WebPConverterData {
      id:           null | string;
      inputFormat:  null | string;
      outputFormat: null | string;
      inputSize:    number | null;
      outputSize:   number | null;
      mimeType:     null | string;
      expires:      number | null;
      downloadURL:  null | string;
  }

  export default class webpconverterWrapper {
    constructor(options: webpconverterOptions);

    execute(callback: (error: any, data: webpconverterResponse | null) => void): Promise<webpconverterResponse>;
    execute(query: Record<string, any>, callback: (error: any, data: webpconverterResponse | null) => void): Promise<webpconverterResponse>;
    execute(query?: Record<string, any>): Promise<webpconverterResponse>;
  }
}
