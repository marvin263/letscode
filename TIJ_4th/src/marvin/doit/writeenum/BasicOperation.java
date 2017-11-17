package marvin.doit.writeenum;

public enum BasicOperation implements Operation {
	PLUS("+") {
		@Override
		public double apply(double x, double y) {
			return 0;
		}

	},
	SUBTRACT("+") {
		@Override
		public double apply(double x, double y) {
			return 0;
		}

	},
	MULTI("+") {
		@Override
		public double apply(double x, double y) {
			return 0;
		}

	},
	DIVIDE("+") {
		@Override
		public double apply(double x, double y) {
			return 0;
		}

	};
	private final String symbol;

	public BasicOperation(String symbol) {
		this.symbol = symbol;
	}
}
