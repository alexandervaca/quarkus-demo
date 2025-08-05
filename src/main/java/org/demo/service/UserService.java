package org.demo.service;

//@ApplicationScoped
//@Slf4j
public class UserService {

    /*@Inject
    UserRepository userRepository;

    public Optional<UserDTO> findById(Long id) {
        log.info("findById - Looking up user with id: {}", id);

        Optional<UserEntity> entityOpt = userRepository.findByIdOptional(id);

        if (entityOpt.isPresent()) {
            log.info("findById - User found with id: {}", id);
            return entityOpt.map(UserEntity::toUser);
        } else {
            log.warn("findById - No user found with id: {}", id);
            return Optional.empty();
        }
    }

    public List<UserDTO> filter(String name, String email, String role) {
        log.info("filter - init. Applying filters -> name: '{}', email: '{}', role: '{}'", name, email, role);

        List<UserEntity> filteredEntities = userRepository.filter(name, email, role);
        log.info("filter - {} users found matching filters", filteredEntities.size());

        List<UserDTO> result = filteredEntities.stream()
                .map(UserEntity::toUser)
                .toList();

        log.info("filter - end. Returning {} user DTOs", result.size());

        return result;
    }

    @Transactional
    public UserDTO create(UserDTO userDTO) {
        log.info("create - init with userDTO: {}", userDTO);

        try {
            UserEntity userEntity = UserEntity.builder()
                    .email(userDTO.getEmail())
                    .role(userDTO.getRole())
                    .name(userDTO.getName())
                    .build();
            userRepository.persist(userEntity);

            log.info("create - entity persisted with ID: {}", userEntity.id);

            var userResponse = userEntity.toUser();

            log.info("create - end for user ID: {}", userResponse.getId());

            return userResponse;

        } catch (PersistenceException pe) {
            log.error("create - error creating entity: {}", pe.getMessage(), pe);

            throw new ErrorCreateEntityException("Error creating entity");
        }
    }

    @Transactional
    public Optional<UserDTO> update(Long id, UserDTO dto) {
        log.info("update - init for user ID: {}", id);

        Optional<UserEntity> existingOpt = userRepository.findByIdOptional(id);

        if (existingOpt.isEmpty()) {
            log.warn("update - user with ID {} not found", id);
            return Optional.empty();
        }

        UserEntity existing = existingOpt.get();
        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setRole(dto.getRole());
        // existing.setSupervisorId(dto.getSupervisorId()); // si aplica

        log.info("update - user with ID {} updated", id);

        return Optional.of(existing.toUser());
    }

    @Transactional
    public boolean deleteById(Long id) {
        log.info("deleteById - Attempting to delete user with id: {}", id);

        boolean deleted = userRepository.deleteByIdIfExists(id);

        if (deleted) {
            log.info("deleteById - User with id {} deleted successfully", id);
        } else {
            log.warn("deleteById - User with id {} not found", id);
        }

        return deleted;
    }
*/
}
