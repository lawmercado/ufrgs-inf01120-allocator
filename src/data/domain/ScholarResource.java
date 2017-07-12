package data.domain;

public enum ScholarResource implements Resource {
	
	PLACES,
	TEACHING_LABORATORY,
	HARDWARE_TEACHING_LABORATORY,
	SOUND_AND_MICROPHONE,
	MEETINGS,
	FORMAL_PRESENTATIONS,
	MOTORIZED_SCREEN,
	EVENTS_AND_SPEECHES,
	CLASSROOM,
	INTRODUCTION_TEACHING_LAB,
	VIDEO_CONFERENCE;
	
	private final int id;
	
	ScholarResource() {
		this.id = ordinal();
	}
	
	@Override
	public int getId() {
		return this.id;
	}
	
	public static ScholarResource fromValue(int value) throws IllegalArgumentException {
        try {
             return ScholarResource.values()[value];
        } catch(ArrayIndexOutOfBoundsException e) {
             throw new IllegalArgumentException("Unknown enum value :" + value);
        }
    }
	

}
