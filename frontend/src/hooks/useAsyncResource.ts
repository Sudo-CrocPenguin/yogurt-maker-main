import { useCallback, useEffect, useState } from "react";

interface AsyncState<T> {
  data: T | null;
  error: unknown;
  loading: boolean;
}

export function useAsyncResource<T>(loader: () => Promise<T>, dependencies: React.DependencyList = []) {
  const [state, setState] = useState<AsyncState<T>>({
    data: null,
    error: null,
    loading: true
  });

  const reload = useCallback(async () => {
    setState((current) => ({ ...current, loading: true, error: null }));
    try {
      const data = await loader();
      setState({ data, error: null, loading: false });
    } catch (error) {
      setState({ data: null, error, loading: false });
    }
  }, dependencies);

  useEffect(() => {
    void reload();
  }, [reload]);

  return { ...state, reload };
}
