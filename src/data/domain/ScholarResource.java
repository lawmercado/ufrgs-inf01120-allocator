package data.domain;

public enum ScholarResource implements Resource {
	
	LaboratorioEnsino(1),
	LaboratorioEnsinoHardware(2),
	SomMicrofone(3),
	Reunioes(4),
	Apresentacoes(5),
	TelaMotorizada(6),
	EventosPalestras(7),
	SalaDeAula(8),
	LaboratiorEnsinoIntroducao(9),
	Videoconferencia(10),
	Lugares(11);
	
	private int id;
	
	ScholarResource(int id) {
		this.id = id;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

}
