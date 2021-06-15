CREATE INDEX idx_kpi_date_desc_type ON public.kpi (date_trunc('day', TIMESTAMP) DESC, "type");
CREATE INDEX idx_kpi_date_second_desc ON public.kpi (date_trunc('second', TIMESTAMP));