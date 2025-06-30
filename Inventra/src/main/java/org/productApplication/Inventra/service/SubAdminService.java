package org.productApplication.Inventra.service;

import org.productApplication.Inventra.models.TblGroups;
import org.productApplication.Inventra.models.TblPermissions;
import org.productApplication.Inventra.models.TblUsers;
import org.productApplication.Inventra.repository.TblGroupsRepository;
import org.productApplication.Inventra.repository.TblPermissionsRepository;
import org.productApplication.Inventra.repository.TblSubAdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class SubAdminService {

    private static final Logger logger = LoggerFactory.getLogger(SubAdminService.class);

    @Autowired
    private TblSubAdminRepository subAdminRepository;

    @Autowired
    private TblGroupsRepository groupsRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private TblPermissionsRepository permissionsRepository;


    private List<TblUsers> users = new ArrayList<>();


    public List<TblPermissions> getPermissionList() {
        return permissionsRepository.findAll();
    }
    public Page<TblUsers> getSubAdminListData(int page, int size, String sortBy, String sortDir) {
//        Sort sort = Sort.by(sortBy);
//        if (sortDir.equalsIgnoreCase("desc")) {
//            sort = sort.descending();
//        }
//        Pageable pageable = PageRequest.of(page, size, sort);
//        return subAdminRepository.getAllRecord(pageable);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return subAdminRepository.getAllRecord(pageable);
    }

    public Optional<TblUsers> getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }
        return subAdminRepository.findById(id);

    }

    public TblUsers getUserByName(String userName) {
        for (TblUsers user : users) {
            if (user.getName().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    public TblUsers updateUser(Long loggedUserId, TblUsers user, Long groupId, List<Long> permissionIds) {

        Optional<TblUsers> existingUserOptinal = subAdminRepository.findById(user.getId());
        if (existingUserOptinal.isEmpty()){
            throw new IllegalArgumentException("User with ID " + user.getId() + " not found.");
        }
        TblUsers existingUser = existingUserOptinal.get();
        existingUser.setName(user.getName());
        Set<TblPermissions> permissions = new HashSet<>();
        if (permissionIds != null) {
            for (Long permissionId : permissionIds) {
                Optional<TblPermissions> permissionsOptional = permissionsRepository.findById(permissionId);
                permissionsOptional.ifPresent(permissions::add);
            }
        }
        existingUser.setPermissions(permissions);
        existingUser.setUpdatedBy(loggedUserId.intValue());
        subAdminRepository.save(existingUser);
        return existingUser;
    }



    public TblUsers getUserByEmail(String email) {
        return subAdminRepository.findByEmail(email);
    }


    public TblUsers saveUser(Long loggedUserId, TblUsers user, Long groupId, List<Long> permissionIds) {
        try {
            Optional<TblGroups> groupsOptional = groupsRepository.findById(groupId);
            if (!groupsOptional.isPresent()) {
                logger.error("Group with ID " + groupId + " not found.");
                return null;
            }
            TblGroups group = groupsOptional.get();
            user.setGroupId(group.getId());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEmail(user.getEmail().toLowerCase());
            user.setName(user.getName().toLowerCase());
            user.setCreatedBy(loggedUserId);
            user.setCreatedAt(Instant.now().toEpochMilli());
            user.setUpdatedBy(loggedUserId.intValue());
            user.setUpdatedAt(Instant.now().toEpochMilli());
            user.setStatus("active");
            user.setIsDeleted("false");

            return subAdminRepository.save(user);
        } catch (Exception e) {
            logger.error("Error saving user: ", e);
            return null;
        }
    }

    public void deleteUser(Long userId) {
        Optional<TblUsers> userToDelete = subAdminRepository.findById(userId);
        if (userToDelete.isPresent()){
            TblUsers user = userToDelete.get();
            user.setIsDeleted("true");
            subAdminRepository.save(user);
        }else {
            throw  new IllegalArgumentException("User with Id "  + userId + "not found.");
        }

    }

    public List<TblGroups> getAllGroups() {
        return groupsRepository.getAllRecordNonDeleted();
    }

    public boolean hasPermission(TblUsers user, String permissionName, Long group) {

        if (user == null) {
            logger.warn("User object is null.  Denying permission {}.", permissionName);
            return false;
        }

        Long groupId = user.getGroupId();

        if (groupId == null) {
            logger.warn("User {} has a null group ID.  Denying permission {}.", user.getId(), permissionName);
            return false;
        }

        Optional<TblGroups> groupOptional = groupsRepository.findById(groupId);

        if (!groupOptional.isPresent()) {
            logger.warn("Group with ID {} not found for user {}. Denying permission {}.", groupId, user.getId(), permissionName);
            return false;
        }

        TblGroups groupEntity = groupOptional.get();

        if (groupEntity.getPermissions() == null || groupEntity.getPermissions().isEmpty()) {
            logger.warn("Group {} has no permissions defined. Denying permission {} for user {}.", groupId, permissionName, user.getId());
            return false;
        }

        Set<TblPermissions> permissions = groupEntity.getPermissions();
        boolean hasPermission = permissions.stream().anyMatch(p -> p.getName().equals(permissionName));

        if (hasPermission) {
            logger.debug("User {} has permission {} via group {}.", user.getId(), permissionName, groupId);
        } else {
            logger.debug("User {} does not have permission {} via group {}.", user.getId(), permissionName, groupId);
        }

        return hasPermission;
    }
    public TblUsers getSubAdminDetails(String email) {
        if (email==null){
        }
        return  subAdminRepository.findByEmail(email);
    }

}
