package ch.admin.bag.covidcertificate.config.cleanup;

import lombok.Data;

@Data
public class SqlQuery {

    /**
     * Counts the existing rows up to the given date.
     * <b>Example</b>
     * <code>select count(*) from billing_kpi where processed_at < ?</code>
     */
    private String count;

    /**
     * Deletes all remaining rows up to given date.
     * <b>Example</b>
     * <code>delete from billing_kpi where processed_at < ?</code>
     */
    private String deleteUntil;

    /**
     * Deletes a batch of rows (of size <code>deleteBatchSize</code>) up to given date.
     * <b>Example</b>
     * <code>delete from billing_kpi where processed_at < (select processed_at from billing_kpi where
     * processed_at < ? order by processed_at asc limit 1 offset ?)</code>
     */
    private String deleteUntilBatch;

    private int deleteUntilBatchSize;
}
