package  proyect_final.clinica.Model.Dto;

import java.time.LocalDate;

public class ConsultaCompletaDTO {
    
    // Datos básicos de la consulta

    private Long idConsulta; 
    private LocalDate fecha;
    private String observaciones;
    private Long idPrestamoActual;
    
    // IDs de relaciones existentes
    private Long idPaciente;
    private Long idEstudiante;
    
    // Datos del Informante
    private String informanteNombres;
    private String informanteApellidoPaterno;
    private String informanteApellidoMaterno;
    private String informanteDireccion;
    private String informanteTelefono;
    
    // Datos de PatologiaPersonal
    private Boolean anemia;
    private Boolean cardiopatias;
    private Boolean enfGastricos;
    private Boolean hepatitis;
    private Boolean tuberculosis;
    private Boolean asma;
    private Boolean diabetesMel;
    private Boolean epilepsia;
    private Boolean hipertension;
    private String otros;
    private Boolean ninguno;
    private Boolean alergias;
    private Boolean embarazo;
    private Integer semanaEmbarazo;

    // Datos de TratamientoMedico
    private String tratamientoMedico;
    private String recibeAlgunMedicamento;
    private Boolean tuvoHemorragiaDental;
    private String especifiqueHemorragia;
    
    // Datos de ExamenExtraOral
    private String atm;
    private String gangliosLinfaticos;
    private String respirador;
    private String otrosRespiratorio;
    
    // Datos de ExamenIntraOral
    private String labios;
    private String lengua;
    private String paladar;
    private String pisoDeLaBoca;
    private String mucosaYugal;
    private String encias;
    private Boolean utilizaProtesisDental;
    private String tipoProtesis;
    private String tiempoProtesis;
    
    // Datos de AntecedentesBucodentales
    private LocalDate fechaRevision;
    private Boolean habitoFuma;
    private Boolean habitoBebe;
    private String otrosHabitos;
    
    // Datos de AntecedentesHigieneOral
    private Boolean utilizaCepilloDental;
    private Boolean utilizaHiloDental;
    private Boolean utilizaEnjuagueBucal;
    private String frecuenciaCepillo;
    private Boolean sangradoEncias;
    private String higieneBucal;
    private String observacionesHigiene;


    // Getters y Setters (generar todos)
    public Long getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(Long idConsulta) {
        this.idConsulta = idConsulta;
    }


    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public Long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Long idPaciente) { this.idPaciente = idPaciente; }
    
    public Long getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(Long idEstudiante) { this.idEstudiante = idEstudiante; }
    
    public String getInformanteNombres() { return informanteNombres; }
    public void setInformanteNombres(String informanteNombres) { this.informanteNombres = informanteNombres; }
    
    public String getInformanteApellidoPaterno() { return informanteApellidoPaterno; }
    public void setInformanteApellidoPaterno(String informanteApellidoPaterno) { this.informanteApellidoPaterno = informanteApellidoPaterno; }
    
    public String getInformanteApellidoMaterno() { return informanteApellidoMaterno; }
    public void setInformanteApellidoMaterno(String informanteApellidoMaterno) { this.informanteApellidoMaterno = informanteApellidoMaterno; }
    
    public String getInformanteDireccion() { return informanteDireccion; }
    public void setInformanteDireccion(String informanteDireccion) { this.informanteDireccion = informanteDireccion; }
    
    public String getInformanteTelefono() { return informanteTelefono; }
    public void setInformanteTelefono(String informanteTelefono) { this.informanteTelefono = informanteTelefono; }
    
    //patologiasPersonales

    public Boolean getAnemia() { return anemia; }
    public void setAnemia(Boolean anemia) { this.anemia = anemia; }
    
    public Boolean getCardiopatias() { return cardiopatias; }
    public void setCardiopatias(Boolean cardiopatias) { this.cardiopatias = cardiopatias; }
    
    public Boolean getHepatitis() { return hepatitis; }
    public void setHepatitis(Boolean hepatitis) { this.hepatitis = hepatitis; }
    
    public Boolean getEnfGastricos() { return enfGastricos; }
    public void setEnfGastricos(Boolean enfGastricos) { this.enfGastricos = enfGastricos; }
    
    public Boolean getTuberculosis() { return tuberculosis; }
    public void setTuberculosis(Boolean tuberculosis) { this.tuberculosis = tuberculosis; }
    
    public Boolean getAsma() { return asma; }
    public void setAsma(Boolean asma) { this.asma = asma; }
    
    public Boolean getDiabetesMel() { return diabetesMel; }
    public void setDiabetesMel(Boolean diabetesMel) { this.diabetesMel = diabetesMel; }
    
    public Boolean getEpilepsia() { return epilepsia; }
    public void setEpilepsia(Boolean epilepsia) { this.epilepsia = epilepsia; }

    public Boolean getHipertension() { return hipertension; }
    public void setHipertension(Boolean hipertension) { this.hipertension = hipertension; }

    public String getOtros() { return otros; }
    public void setOtros(String otros) { this.otros = otros; }

    public Boolean getNinguno() { return ninguno; }
    public void setNinguno(Boolean ninguno) { this.ninguno = ninguno; }


    public Boolean getAlergias() { return alergias; }
    public void setAlergias(Boolean alergias) { this.alergias = alergias; }

    public Boolean getEmbarazo() { return embarazo; }
    public void setEmbarazo(Boolean embarazo) { this.embarazo = embarazo; }

    public Integer getSemanaEmbarazo() { return semanaEmbarazo; }
    public void setSemanaEmbarazo(Integer semanaEmbarazo) { this.semanaEmbarazo = semanaEmbarazo; }

    

    public String getTratamientoMedico() { return tratamientoMedico; }
    public void setTratamientoMedico(String tratamientoMedico) { this.tratamientoMedico = tratamientoMedico; }

    public String getRecibeAlgunMedicamento() { return recibeAlgunMedicamento; }
    public void setRecibeAlgunMedicamento(String recibeAlgunMedicamento) { this.recibeAlgunMedicamento = recibeAlgunMedicamento; }
    
    public Boolean getTuvoHemorragiaDental() { return tuvoHemorragiaDental; }
    public void setTuvoHemorragiaDental(Boolean tuvoHemorragiaDental) { this.tuvoHemorragiaDental = tuvoHemorragiaDental; }
    
    public String getEspecifiqueHemorragia() { return especifiqueHemorragia; }
    public void setEspecifiqueHemorragia(String especifiqueHemorragia) { this.especifiqueHemorragia = especifiqueHemorragia; }
    
    public String getAtm() { return atm; }
    public void setAtm(String atm) { this.atm = atm; }
    
    public String getGangliosLinfaticos() { return gangliosLinfaticos; }
    public void setGangliosLinfaticos(String gangliosLinfaticos) { this.gangliosLinfaticos = gangliosLinfaticos; }
    
    public String getRespirador() { return respirador; }
    public void setRespirador(String respirador) { this.respirador = respirador; }
    
    public String getOtrosRespiratorio() { return otrosRespiratorio; }
    public void setOtrosRespiratorio(String otrosRespiratorio) { this.otrosRespiratorio = otrosRespiratorio; }
    
    public String getLabios() { return labios; }
    public void setLabios(String labios) { this.labios = labios; }
    
    public String getLengua() { return lengua; }
    public void setLengua(String lengua) { this.lengua = lengua; }
    
    public String getPaladar() { return paladar; }
    public void setPaladar(String paladar) { this.paladar = paladar; }
    
    public String getPisoDeLaBoca() { return pisoDeLaBoca; }
    public void setPisoDeLaBoca(String pisoDeLaBoca) { this.pisoDeLaBoca = pisoDeLaBoca; }
    
    public String getMucosaYugal() { return mucosaYugal; }
    public void setMucosaYugal(String mucosaYugal) { this.mucosaYugal = mucosaYugal; }
    
    public String getEncias() { return encias; }
    public void setEncias(String encias) { this.encias = encias; }
    
    public Boolean getUtilizaProtesisDental() { return utilizaProtesisDental; }
    public void setUtilizaProtesisDental(Boolean utilizaProtesisDental) { this.utilizaProtesisDental = utilizaProtesisDental; }
    
    public String getTipoProtesis() { return tipoProtesis; }
    public void setTipoProtesis(String tipoProtesis) { this.tipoProtesis = tipoProtesis; }
    
    public String getTiempoProtesis() { return tiempoProtesis; }
    public void setTiempoProtesis(String tiempoProtesis) { this.tiempoProtesis = tiempoProtesis; }
    
    public LocalDate getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(LocalDate fechaRevision) { this.fechaRevision = fechaRevision; }
    
    public Boolean getHabitoFuma() { return habitoFuma; }
    public void setHabitoFuma(Boolean habitoFuma) { this.habitoFuma = habitoFuma; }
    
    public Boolean getHabitoBebe() { return habitoBebe; }
    public void setHabitoBebe(Boolean habitoBebe) { this.habitoBebe = habitoBebe; }
    
    public String getOtrosHabitos() { return otrosHabitos; }
    public void setOtrosHabitos(String otrosHabitos) { this.otrosHabitos = otrosHabitos; }
    
    public Boolean getUtilizaCepilloDental() { return utilizaCepilloDental; }
    public void setUtilizaCepilloDental(Boolean utilizaCepilloDental) { this.utilizaCepilloDental = utilizaCepilloDental; }
    
    public Boolean getUtilizaHiloDental() { return utilizaHiloDental; }
    public void setUtilizaHiloDental(Boolean utilizaHiloDental) { this.utilizaHiloDental = utilizaHiloDental; }
    
    public Boolean getUtilizaEnjuagueBucal() { return utilizaEnjuagueBucal; }
    public void setUtilizaEnjuagueBucal(Boolean utilizaEnjuagueBucal) { this.utilizaEnjuagueBucal = utilizaEnjuagueBucal; }
    
    public String getFrecuenciaCepillo() { return frecuenciaCepillo; }
    public void setFrecuenciaCepillo(String frecuenciaCepillo) { this.frecuenciaCepillo = frecuenciaCepillo; }
    
    public Boolean getSangradoEncias() { return sangradoEncias; }
    public void setSangradoEncias(Boolean sangradoEncias) { this.sangradoEncias = sangradoEncias; }
    
    public String getHigieneBucal() { return higieneBucal; }
    public void setHigieneBucal(String higieneBucal) { this.higieneBucal = higieneBucal; }
    
    public String getObservacionesHigiene() { return observacionesHigiene; }
    public void setObservacionesHigiene(String observacionesHigiene) { this.observacionesHigiene = observacionesHigiene; }

    public Long getIdPrestamoActual() { return idPrestamoActual; }
    public void setIdPrestamoActual(Long idPrestamoActual) { this.idPrestamoActual = idPrestamoActual; }

}