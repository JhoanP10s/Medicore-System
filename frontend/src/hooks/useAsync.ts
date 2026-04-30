import { useCallback, useEffect, useState } from 'react';
import { getErrorMessage } from '../api/http';

export function useAsync<T>(loader: () => Promise<T>, immediate = true) {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(immediate);
  const [error, setError] = useState<string | null>(null);

  const execute = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await loader();
      setData(result);
      return result;
    } catch (err) {
      setError(getErrorMessage(err));
      throw err;
    } finally {
      setLoading(false);
    }
  }, [loader]);

  useEffect(() => {
    if (immediate) {
      execute().catch(() => undefined);
    }
  }, [execute, immediate]);

  return { data, setData, loading, error, execute };
}
