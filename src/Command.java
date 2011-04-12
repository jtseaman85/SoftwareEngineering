
public enum Command {
	MOVE(0), NEW(1), RESTORE(2), RESIGN(3), SAVE(4), DRAW(5);
	
	private int code;
	
	private Command(int i) {
		code = i;
	}
	
	public int toInt() {
		return code;
	}

	@Override
	public String toString() {
		return Integer.toString(code);
	}
}
