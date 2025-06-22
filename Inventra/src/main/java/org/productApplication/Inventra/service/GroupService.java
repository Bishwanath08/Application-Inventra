package org.productApplication.Inventra.service;

import org.productApplication.Inventra.models.TblGroups;
import org.productApplication.Inventra.models.TblPermissions;
import org.productApplication.Inventra.models.TblUsers;
import org.productApplication.Inventra.repository.TblGroupPermissionRepository;
import org.productApplication.Inventra.repository.TblGroupsRepository;
import org.productApplication.Inventra.repository.TblPermissionsRepository;
import org.productApplication.Inventra.repository.TblUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service
public class GroupService {

    @Autowired
    private TblGroupsRepository groupsRepository;

    @Autowired
    private TblGroupPermissionRepository groupPermissionRepository;

    @Autowired
    private TblPermissionsRepository permissionsRepository;

    @Autowired
    private TblUsersRepository usersRepository;

    List<TblGroups> groups = new ArrayList<>();

    public Optional<TblPermissions> getPermissionById(Long id) {
        return permissionsRepository.findById(id);
    }


    public List<TblPermissions> getPermissionList() {
        return permissionsRepository.findAll();
    }

    public Page<TblGroups> getGroupListData(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(sortBy);
        if (sortDir.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return groupsRepository.getAllRecord(pageable);

    }

    public Optional<TblGroups> getGroupById(Long groupId) {
        return groupsRepository.findById(groupId)
                .filter(group -> !"true".equals(group.getIsDeleted()));
    }

    public TblGroups getGroupByName(String groupName) {
//        for (TblGroups group : groups) {
//            if (group.getName().equals(groupName)) {
//                return group;
//            }
//        }
//        return null;
        return groupsRepository.findByName(groupName);
    }

    public void updateGroup(Long userId, TblGroups groups, List<Long> permissionIds) {
        Optional<TblGroups> existingGroupOptinal = groupsRepository.findById(groups.getId());
        if (existingGroupOptinal.isEmpty()){
            throw new IllegalArgumentException("Group with ID " + groups.getId() + " not found.");
        }
        TblGroups existingGroup = existingGroupOptinal.get();
        existingGroup.setName(groups.getName());

        Set<TblPermissions> permissions = new HashSet<>();
        if (permissionIds != null) {
            for (Long permissionId : permissionIds) {
                Optional<TblPermissions> permissionsOptional = permissionsRepository.findById(permissionId);
                permissionsOptional.ifPresent(permissions::add);
            }
        }
        existingGroup.setPermissions(permissions);
        Optional<TblUsers> updator = usersRepository.findById(userId);
        updator.ifPresent(existingGroup::setCreator);
        groupsRepository.save(existingGroup);
    }


    public void deleteGroup(Long groupId) {
        Optional<TblGroups> groupToDelete = groupsRepository.findById(groupId);
        if (groupToDelete.isPresent()) {
            TblGroups group = groupToDelete.get();
            group.setIsDeleted("true");
            groupsRepository.save(group);
        } else {
            throw new IllegalArgumentException("Group with ID " + groupId + " not found.");
        }
    }


    public List<TblPermissions> getAllPermissions() {
        return permissionsRepository.findAll();
    }

//    public Set<TblPermissions> getGroupForPermissions(Long groupId) {
//        Optional<TblGroups> optionalGroup = groupsRepository.findById(groupId);
//        if (optionalGroup.isPresent()) {
//            return optionalGroup.get().getPermissions();
//        } else {
//            throw new IllegalArgumentException("Group with ID " + groupId + " not found.");
//        }
//    }


    public List<String> getPermissionsForGroup(Long groupId) {
        Optional<TblGroups> optionalGroup = groupsRepository.findById(groupId);
        List<String> permissionNames = new ArrayList<>();
        for (TblPermissions permission : optionalGroup.get().getPermissions()) {
            permissionNames.add(permission.getName());
        }
            return permissionNames;
//        if (optionalGroup.isPresent()) {
//            return optionalGroup.get().getPermissions();
//        } else {
//            throw new IllegalArgumentException("Group not found with ID: " + groupId);
//        }
    }


    public TblGroups saveGroupWithPermission(Long loggedUserId, String groupName, List<Long> permissionIds) {
        TblGroups group = new TblGroups();
        group.setName(groupName);
        group.setCreatedBy(loggedUserId.intValue());
        group.setCreatedAt(Instant.now().toEpochMilli());
        group.setUpdatedBy(loggedUserId.intValue());
        group.setUpdatedAt(Instant.now().toEpochMilli());
        group.setStatus("active");
        group.setIsDeleted("false");

        Set<TblPermissions> permissions = new HashSet<>();
        if (permissionIds != null) {
            for (Long permissionId : permissionIds) {
                Optional<TblPermissions> permissionsOptional = permissionsRepository.findById(permissionId);
                permissionsOptional.ifPresent(permissions::add);
            }
        }
        group.setPermissions(permissions);

        Optional<TblUsers> creator = usersRepository.findById(loggedUserId);
        creator.ifPresent(group::setCreator);

        return groupsRepository.save(group);
    }

}

