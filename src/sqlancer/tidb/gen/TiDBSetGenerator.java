package sqlancer.tidb.gen;

import java.sql.SQLException;
import java.util.function.Function;

import sqlancer.Query;
import sqlancer.QueryAdapter;
import sqlancer.Randomly;
import sqlancer.tidb.TiDBProvider.TiDBGlobalState;

public class TiDBSetGenerator {

	private enum Action {

		TIDB_OPT_AGG_PUSH_DOWN("tidb_opt_agg_push_down", (r) -> Randomly.fromOptions(0, 1)),
		TIDB_BUILD_STATS_CONCURRENCY("tidb_build_stats_concurrency", (r) -> Randomly.getNotCachedInteger(0, 500)),
		TIDB_DISTSQL_SCAN_CONCURRENCY("tidb_distsql_scan_concurrency", (r) -> Randomly.getNotCachedInteger(1, 500)),
		TIDB_INDEX_LOOKUP_SIZE("tidb_index_lookup_size", (r) -> Randomly.getNotCachedInteger(1, 100000)),
		TIDB_INDEX_LOOKUP_CONCURRENCY("tidb_index_lookup_concurrency", (r) -> Randomly.getNotCachedInteger(1, 100)),
		TIDB_INDEX_JOIN_BATCH_SIZE("tidb_index_join_batch_size", (r) -> Randomly.getNotCachedInteger(1, 5000)), //
		TIDB_INIT_CHUNK_SIZE("tidb_init_chunk_size", (r) -> Randomly.getNotCachedInteger(1, 32)),
		TIDB_MAX_CHUNK_SIZE("tidb_max_chunk_size", (r) -> Randomly.getNotCachedInteger(32, 50000)),
		TIDB_OPT_INSUBQ_TO_JOIN_AND_AGG("tidb_opt_insubq_to_join_and_agg", (r) -> Randomly.fromOptions(0, 1)),
		TIDB_OPT_CORRELATION_THRESHOLD("tidb_opt_correlation_threshold",
				(r) -> Randomly.fromOptions(0, 0.0001, 0.1, 0.25, 0.50, 0.75, 0.9, 0.9999999, 1)),
		TIDB_OPT_CORRELATION_EXP_FACTOR("tidb_opt_correlation_exp_factor",
				(r) -> Randomly.getNotCachedInteger(0, 10000));
		// https://github.com/pingcap/tidb/issues/15751
		// https://github.com/pingcap/tidb/issues/15752
//		TIDB_ENABLE_FAST_ANALYZE("tidb_enable_fast_analyze", (r) -> Randomly.fromOptions(0, 1));

		private String name;
		private Function<Randomly, Object> prod;

		Action(String name, Function<Randomly, Object> prod) {
			this.name = name;
			this.prod = prod;
		}

	}

	public static Query getQuery(TiDBGlobalState globalState) throws SQLException {
		StringBuilder sb = new StringBuilder();
		Action option = Randomly.fromOptions(Action.values());
		sb.append("set @@");
		sb.append(option.name);
		sb.append("=");
		sb.append(option.prod.apply(globalState.getRandomly()));
		return new QueryAdapter(sb.toString());
	}

}