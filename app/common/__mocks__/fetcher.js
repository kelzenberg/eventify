const originalFetcher = jest.requireActual("../fetcher");

const request = jest.fn();

export default {
    FetchError: originalFetcher.default.FetchError,
    request: request,
    status: originalFetcher.default.status
}