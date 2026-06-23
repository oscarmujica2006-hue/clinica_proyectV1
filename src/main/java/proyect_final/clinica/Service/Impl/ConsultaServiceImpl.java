package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Entity.*;
import proyect_final.clinica.Model.Dto.ConsultaCompletaDTO;
import proyect_final.clinica.Model.Dao.*;
import proyect_final.clinica.Service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import proyect_final.clinica.Model.Dao.PrestamoActualRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import proyect_final.clinica.Model.Dto.ConsultaConFotosDTO;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ConsultaServiceImpl implements ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private OdontogramaFotoRepository odontogramaFotoRepository;

    @Autowired
    private PrestamoActualRepository prestamoActualRepository;

    // ========== MÉTODOS EXISTENTES (sin cambios) ==========

    @Override
    public List<Consulta> obtenerTodos() {
        return consultaRepository.findAll();
    }

    @Override
    public Optional<Consulta> obtenerPorId(Long id) {
        return consultaRepository.findById(id);
    }

    @Override
    public Consulta guardar(Consulta consulta) {
        return consultaRepository.save(consulta);
    }

    @Override
    @Transactional
    public Consulta guardarConsultaCompleta(ConsultaCompletaDTO consultaDTO) {

        Paciente paciente = pacienteRepository.findById(consultaDTO.getIdPaciente())
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Estudiante estudiante = estudianteRepository.findById(consultaDTO.getIdEstudiante())
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Informante informante = new Informante();
        informante.setNombres(consultaDTO.getInformanteNombres());
        informante.setApellidoPaterno(consultaDTO.getInformanteApellidoPaterno());
        informante.setApellidoMaterno(consultaDTO.getInformanteApellidoMaterno());
        informante.setDireccion(consultaDTO.getInformanteDireccion());
        informante.setTelefono(consultaDTO.getInformanteTelefono());

        PatologiaPersonal patologiaPersonal = new PatologiaPersonal();
        patologiaPersonal.setAnemia(consultaDTO.getAnemia());
        patologiaPersonal.setCardiopatias(consultaDTO.getCardiopatias());
        patologiaPersonal.setEnfGastricos(consultaDTO.getEnfGastricos());
        patologiaPersonal.setHepatitis(consultaDTO.getHepatitis());
        patologiaPersonal.setTuberculosis(consultaDTO.getTuberculosis());
        patologiaPersonal.setAsma(consultaDTO.getAsma());
        patologiaPersonal.setDiabetesMel(consultaDTO.getDiabetesMel());
        patologiaPersonal.setEpilepsia(consultaDTO.getEpilepsia());
        patologiaPersonal.setHipertension(consultaDTO.getHipertension());
        patologiaPersonal.setOtros(consultaDTO.getOtros());
        patologiaPersonal.setNinguno(consultaDTO.getNinguno());
        patologiaPersonal.setAlergias(consultaDTO.getAlergias());
        patologiaPersonal.setEmbarazo(consultaDTO.getEmbarazo());
        patologiaPersonal.setSemanaEmbarazo(consultaDTO.getSemanaEmbarazo());

        TratamientoMedico tratamientoMedico = new TratamientoMedico();
        tratamientoMedico.setTratamientoMedico(consultaDTO.getTratamientoMedico());
        tratamientoMedico.setRecibeAlgunMedicamento(consultaDTO.getRecibeAlgunMedicamento());
        tratamientoMedico.setTuvoHemorragiaDental(consultaDTO.getTuvoHemorragiaDental());
        tratamientoMedico.setEspecifiqueHemorragia(consultaDTO.getEspecifiqueHemorragia());

        ExamenExtraOral examenExtraOral = new ExamenExtraOral();
        examenExtraOral.setAtm(consultaDTO.getAtm());
        examenExtraOral.setGangliosLinfaticos(consultaDTO.getGangliosLinfaticos());
        examenExtraOral.setRespirador(consultaDTO.getRespirador());

        ExamenIntraOral examenIntraOral = new ExamenIntraOral();
        examenIntraOral.setLabios(consultaDTO.getLabios());
        examenIntraOral.setLengua(consultaDTO.getLengua());
        examenIntraOral.setPaladar(consultaDTO.getPaladar());
        examenIntraOral.setPisoDeLaBoca(consultaDTO.getPisoDeLaBoca());
        examenIntraOral.setMucosaYugal(consultaDTO.getMucosaYugal());
        examenIntraOral.setEncias(consultaDTO.getEncias());
        examenIntraOral.setUtilizaProtesisDental(consultaDTO.getUtilizaProtesisDental());

        AntecedenteBucodental antecedentesBucodentales = new AntecedenteBucodental();
        antecedentesBucodentales.setFechaRevision(consultaDTO.getFechaRevision());
        antecedentesBucodentales.setFuma(consultaDTO.getHabitoFuma());
        antecedentesBucodentales.setBebe(consultaDTO.getHabitoBebe());
        antecedentesBucodentales.setOtrosHabitos(consultaDTO.getOtrosHabitos());

        AntecedenteHigieneOral antecedentesHigieneOral = new AntecedenteHigieneOral();
        antecedentesHigieneOral.setUtilizaCepilloDental(consultaDTO.getUtilizaCepilloDental());
        antecedentesHigieneOral.setUtilizaHiloDental(consultaDTO.getUtilizaHiloDental());
        antecedentesHigieneOral.setUtilizaEnjuagueBucal(consultaDTO.getUtilizaEnjuagueBucal());
        antecedentesHigieneOral.setFrecuenciaCepillo(consultaDTO.getFrecuenciaCepillo());

        String duranteElCepillado = Boolean.TRUE.equals(consultaDTO.getSangradoEncias()) ? "Sangrado de encías" : "Sin sangrado";
        antecedentesHigieneOral.setDuranteElCepillado(duranteElCepillado);
        antecedentesHigieneOral.setHigieneBucal(consultaDTO.getHigieneBucal());

        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setEstudiante(estudiante);
        consulta.setInformante(informante);
        consulta.setPatologiaPersonal(patologiaPersonal);
        consulta.setTratamientoMedico(tratamientoMedico);
        consulta.setExamenExtraOral(examenExtraOral);
        consulta.setExamenIntraOral(examenIntraOral);
        consulta.setAntecedentesBucodentales(antecedentesBucodentales);
        consulta.setAntecedentesHigieneOral(antecedentesHigieneOral);

        return consultaRepository.save(consulta);
    }

    @Override
    public void eliminar(Long id) {
        consultaRepository.deleteById(id);
    }



    @Override
    public List<Consulta> obtenerPorPaciente(Long idPaciente) {
        return consultaRepository.findByPacienteIdPaciente(idPaciente);
    }

    @Override
    public List<Consulta> obtenerPorEstudiante(Long idEstudiante) {
        return consultaRepository.findByEstudianteIdEstudiante(idEstudiante);
    }

    // @Override
    // public List<Consulta> obtenerPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
    //     return consultaRepository.findByFechaBetween(fechaInicio, fechaFin);
    // }

    @Override
    public List<Consulta> buscarPorCriterio(String criterio) {
        return consultaRepository.buscarPorCriterio(criterio);
    }

    @Override
    public Optional<Consulta> obtenerConsultaCompleta(Long idConsulta) {
        return consultaRepository.findById(idConsulta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultaCompletaDTO> obtenerConsultasCompletasPorPaciente(Long idPaciente) {
        try {
            List<Consulta> consultas = consultaRepository.findByPacienteIdPacienteWithRelations(idPaciente);
            List<ConsultaCompletaDTO> dtos = new ArrayList<>();

            for (Consulta consulta : consultas) {
                ConsultaCompletaDTO dto = new ConsultaCompletaDTO();
                dto.setIdConsulta(consulta.getIdConsulta());
                dto.setIdPaciente(consulta.getPaciente().getIdPaciente());
                dto.setIdEstudiante(consulta.getEstudiante().getIdEstudiante());

                if (consulta.getInformante() != null) {
                    dto.setInformanteNombres(consulta.getInformante().getNombres());
                    dto.setInformanteApellidoPaterno(consulta.getInformante().getApellidoPaterno());
                    dto.setInformanteApellidoMaterno(consulta.getInformante().getApellidoMaterno());
                    dto.setInformanteDireccion(consulta.getInformante().getDireccion());
                    dto.setInformanteTelefono(consulta.getInformante().getTelefono());
                }

                if (consulta.getPatologiaPersonal() != null) {
                    PatologiaPersonal p = consulta.getPatologiaPersonal();
                    dto.setAnemia(p.getAnemia());
                    dto.setCardiopatias(p.getCardiopatias());
                    dto.setEnfGastricos(p.getEnfGastricos());
                    dto.setHepatitis(p.getHepatitis());
                    dto.setTuberculosis(p.getTuberculosis());
                    dto.setAsma(p.getAsma());
                    dto.setDiabetesMel(p.getDiabetesMel());
                    dto.setEpilepsia(p.getEpilepsia());
                    dto.setHipertension(p.getHipertension());
                    dto.setOtros(p.getOtros());
                    dto.setNinguno(p.getNinguno());
                    dto.setAlergias(p.getAlergias());
                    dto.setEmbarazo(p.getEmbarazo());
                    dto.setSemanaEmbarazo(p.getSemanaEmbarazo());
                }

                if (consulta.getTratamientoMedico() != null) {
                    dto.setTratamientoMedico(consulta.getTratamientoMedico().getTratamientoMedico());
                    dto.setRecibeAlgunMedicamento(consulta.getTratamientoMedico().getRecibeAlgunMedicamento());
                    dto.setTuvoHemorragiaDental(consulta.getTratamientoMedico().getTuvoHemorragiaDental());
                    dto.setEspecifiqueHemorragia(consulta.getTratamientoMedico().getEspecifiqueHemorragia());
                }

                if (consulta.getExamenExtraOral() != null) {
                    dto.setAtm(consulta.getExamenExtraOral().getAtm());
                    dto.setGangliosLinfaticos(consulta.getExamenExtraOral().getGangliosLinfaticos());
                    dto.setRespirador(consulta.getExamenExtraOral().getRespirador());
                }

                if (consulta.getExamenIntraOral() != null) {
                    dto.setLabios(consulta.getExamenIntraOral().getLabios());
                    dto.setLengua(consulta.getExamenIntraOral().getLengua());
                    dto.setPaladar(consulta.getExamenIntraOral().getPaladar());
                    dto.setPisoDeLaBoca(consulta.getExamenIntraOral().getPisoDeLaBoca());
                    dto.setMucosaYugal(consulta.getExamenIntraOral().getMucosaYugal());
                    dto.setEncias(consulta.getExamenIntraOral().getEncias());
                    dto.setUtilizaProtesisDental(consulta.getExamenIntraOral().getUtilizaProtesisDental());
                }

                if (consulta.getAntecedentesBucodentales() != null) {
                    dto.setFechaRevision(consulta.getAntecedentesBucodentales().getFechaRevision());
                    dto.setHabitoFuma(consulta.getAntecedentesBucodentales().getFuma());
                    dto.setHabitoBebe(consulta.getAntecedentesBucodentales().getBebe());
                    dto.setOtrosHabitos(consulta.getAntecedentesBucodentales().getOtrosHabitos());
                }

                if (consulta.getAntecedentesHigieneOral() != null) {
                    dto.setUtilizaCepilloDental(consulta.getAntecedentesHigieneOral().getUtilizaCepilloDental());
                    dto.setUtilizaHiloDental(consulta.getAntecedentesHigieneOral().getUtilizaHiloDental());
                    dto.setUtilizaEnjuagueBucal(consulta.getAntecedentesHigieneOral().getUtilizaEnjuagueBucal());
                    dto.setFrecuenciaCepillo(consulta.getAntecedentesHigieneOral().getFrecuenciaCepillo());
                    dto.setSangradoEncias(consulta.getAntecedentesHigieneOral().getDuranteElCepillado() != null &&
                                         consulta.getAntecedentesHigieneOral().getDuranteElCepillado().contains("Sangrado"));
                    dto.setHigieneBucal(consulta.getAntecedentesHigieneOral().getHigieneBucal());
                }

                dtos.add(dto);
            }

            return dtos;
        } catch (Exception e) {
            System.out.println("❌ Error en obtenerConsultasCompletasPorPaciente: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener consultas completas: " + e.getMessage(), e);
        }
    }

    // ========== MÉTODO PARA GUARDAR CONSULTA CON FOTOS (MANTENIDO) ==========
    @Override
    public Consulta guardarConsultaConFotos(ConsultaConFotosDTO dto) throws IOException {

        if (dto.getIdPrestamoActual() == null) {
            throw new RuntimeException("ID del préstamo actual es requerido");
        }

        PrestamoActual prestamoActual = prestamoActualRepository.findById(dto.getIdPrestamoActual())
            .orElseThrow(() -> new RuntimeException("Préstamo actual no encontrado con ID: " + dto.getIdPrestamoActual()));

        if (!prestamoActual.getIdEstudiante().equals(dto.getIdEstudiante())) {
            throw new RuntimeException("El préstamo no pertenece al estudiante actual");
        }

        Consulta consulta = convertirDTOaEntidad(dto);
        consulta = consultaRepository.save(consulta);

        MultipartFile[] fotos = dto.getOdontogramaFotos();
        if (fotos != null) {
            for (MultipartFile foto : fotos) {
                if (!foto.isEmpty()) {
                    guardarFotoConsulta(consulta.getIdConsulta(), foto);
                }
            }
        }

        return consulta;
    }

    // ========== NUEVO MÉTODO PARA GUARDAR UNA SOLA FOTO ==========
    @Override
    @Transactional
    public void guardarFotoConsulta(Long idConsulta, MultipartFile archivo) throws IOException {
        Consulta consulta = consultaRepository.findById(idConsulta)
            .orElseThrow(() -> new RuntimeException("Consulta no encontrada con ID: " + idConsulta));

        String contentType = archivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen");
        }

        String nombreOriginal = archivo.getOriginalFilename();
        String extension = "";
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        }
        String nombreArchivo = UUID.randomUUID().toString() + extension;

        Path directorio = Paths.get("uploads/odontograma/");
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
        }
        Path rutaCompleta = directorio.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

        OdontogramaFoto fotoEntidad = new OdontogramaFoto();
        fotoEntidad.setConsulta(consulta);
        fotoEntidad.setRutaArchivo("/uploads/odontograma/" + nombreArchivo);
        fotoEntidad.setNombreOriginal(nombreOriginal);
        fotoEntidad.setTipoContenido(contentType);
        fotoEntidad.setTamano(archivo.getSize());

        odontogramaFotoRepository.save(fotoEntidad);
    }

    private Consulta convertirDTOaEntidad(ConsultaCompletaDTO dto) {
        Paciente paciente = pacienteRepository.findById(dto.getIdPaciente())
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        Estudiante estudiante = estudianteRepository.findById(dto.getIdEstudiante())
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Informante informante = new Informante();
        informante.setNombres(dto.getInformanteNombres());
        informante.setApellidoPaterno(dto.getInformanteApellidoPaterno());
        informante.setApellidoMaterno(dto.getInformanteApellidoMaterno());
        informante.setDireccion(dto.getInformanteDireccion());
        informante.setTelefono(dto.getInformanteTelefono());

        PatologiaPersonal patologiaPersonal = new PatologiaPersonal();
        patologiaPersonal.setAnemia(dto.getAnemia());
        patologiaPersonal.setCardiopatias(dto.getCardiopatias());
        patologiaPersonal.setEnfGastricos(dto.getEnfGastricos());
        patologiaPersonal.setHepatitis(dto.getHepatitis());
        patologiaPersonal.setTuberculosis(dto.getTuberculosis());
        patologiaPersonal.setAsma(dto.getAsma());
        patologiaPersonal.setDiabetesMel(dto.getDiabetesMel());
        patologiaPersonal.setEpilepsia(dto.getEpilepsia());
        patologiaPersonal.setHipertension(dto.getHipertension());
        patologiaPersonal.setOtros(dto.getOtros());
        patologiaPersonal.setNinguno(dto.getNinguno());
        patologiaPersonal.setAlergias(dto.getAlergias());
        patologiaPersonal.setEmbarazo(dto.getEmbarazo());
        patologiaPersonal.setSemanaEmbarazo(dto.getSemanaEmbarazo());

        TratamientoMedico tratamientoMedico = new TratamientoMedico();
        tratamientoMedico.setTratamientoMedico(dto.getTratamientoMedico());
        tratamientoMedico.setRecibeAlgunMedicamento(dto.getRecibeAlgunMedicamento());
        tratamientoMedico.setTuvoHemorragiaDental(dto.getTuvoHemorragiaDental());
        tratamientoMedico.setEspecifiqueHemorragia(dto.getEspecifiqueHemorragia());

        ExamenExtraOral examenExtraOral = new ExamenExtraOral();
        examenExtraOral.setAtm(dto.getAtm());
        examenExtraOral.setGangliosLinfaticos(dto.getGangliosLinfaticos());
        examenExtraOral.setRespirador(dto.getRespirador());

        ExamenIntraOral examenIntraOral = new ExamenIntraOral();
        examenIntraOral.setLabios(dto.getLabios());
        examenIntraOral.setLengua(dto.getLengua());
        examenIntraOral.setPaladar(dto.getPaladar());
        examenIntraOral.setPisoDeLaBoca(dto.getPisoDeLaBoca());
        examenIntraOral.setMucosaYugal(dto.getMucosaYugal());
        examenIntraOral.setEncias(dto.getEncias());
        examenIntraOral.setUtilizaProtesisDental(dto.getUtilizaProtesisDental());

        AntecedenteBucodental antecedentesBucodentales = new AntecedenteBucodental();
        antecedentesBucodentales.setFechaRevision(dto.getFechaRevision());
        antecedentesBucodentales.setFuma(dto.getHabitoFuma());
        antecedentesBucodentales.setBebe(dto.getHabitoBebe());
        antecedentesBucodentales.setOtrosHabitos(dto.getOtrosHabitos());

        AntecedenteHigieneOral antecedentesHigieneOral = new AntecedenteHigieneOral();
        antecedentesHigieneOral.setUtilizaCepilloDental(dto.getUtilizaCepilloDental());
        antecedentesHigieneOral.setUtilizaHiloDental(dto.getUtilizaHiloDental());
        antecedentesHigieneOral.setUtilizaEnjuagueBucal(dto.getUtilizaEnjuagueBucal());
        antecedentesHigieneOral.setFrecuenciaCepillo(dto.getFrecuenciaCepillo());
        String duranteElCepillado = Boolean.TRUE.equals(dto.getSangradoEncias()) ? "Sangrado de encías" : "Sin sangrado";
        antecedentesHigieneOral.setDuranteElCepillado(duranteElCepillado);
        antecedentesHigieneOral.setHigieneBucal(dto.getHigieneBucal());

        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setEstudiante(estudiante);
        consulta.setInformante(informante);
        consulta.setPatologiaPersonal(patologiaPersonal);
        consulta.setTratamientoMedico(tratamientoMedico);
        consulta.setExamenExtraOral(examenExtraOral);
        consulta.setExamenIntraOral(examenIntraOral);
        consulta.setAntecedentesBucodentales(antecedentesBucodentales);
        consulta.setAntecedentesHigieneOral(antecedentesHigieneOral);

        return consulta;
    }
}