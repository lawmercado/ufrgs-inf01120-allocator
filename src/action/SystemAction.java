package action;

public abstract class SystemAction implements Action {

	private boolean enabled;

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

	}
	
	@Override
	public boolean isEnabled() {
		return this.enabled;

	}

}
