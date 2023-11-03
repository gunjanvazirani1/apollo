package com.ctrip.framework.apollo.adminservice.aop;

import com.ctrip.framework.apollo.biz.config.BizConfig;
import com.ctrip.framework.apollo.biz.entity.Item;
import com.ctrip.framework.apollo.biz.entity.Namespace;
import com.ctrip.framework.apollo.biz.service.ItemService;
import com.ctrip.framework.apollo.biz.service.NamespaceLockService;
import com.ctrip.framework.apollo.biz.service.NamespaceService;
import com.ctrip.framework.apollo.common.dto.ItemChangeSets;
import com.ctrip.framework.apollo.common.dto.ItemDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class NamespaceAcquireLockAspectTest {

    @Mock
    private NamespaceLockService namespaceLockService;

    @Mock
    private NamespaceService namespaceService;

    @Mock
    private ItemService itemService;

    @Mock
    private BizConfig bizConfig;

    private NamespaceAcquireLockAspect lockAspect;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        lockAspect = new NamespaceAcquireLockAspect(namespaceLockService, namespaceService, itemService, bizConfig);
    }

    @Test
    public void testRequireLockAdviceForCreateItem() {
        String appId = "testApp";
        String clusterName = "testCluster";
        String namespaceName = "testNamespace";
        ItemDTO item = new ItemDTO();
        item.setDataChangeLastModifiedBy("testUser");

        when(bizConfig.isNamespaceLockSwitchOff()).thenReturn(false);

        Namespace mockNamespace = new Namespace();
        when(namespaceService.findOne(appId, clusterName, namespaceName)).thenReturn(mockNamespace);

        when(namespaceLockService.findLock(mockNamespace.getId())).thenReturn(null);

        lockAspect.requireLockAdvice(appId, clusterName, namespaceName, item);

        verify(namespaceLockService, times(1)).tryLock(argThat(lock ->
                lock.getNamespaceId() == mockNamespace.getId() && lock.getDataChangeCreatedBy().equals("testUser")));
    }

    @Test
    public void testRequireLockAdviceForUpdateItem() {
        String appId = "testApp";
        String clusterName = "testCluster";
        String namespaceName = "testNamespace";
        long itemId = 123;
        ItemDTO item = new ItemDTO();
        item.setDataChangeLastModifiedBy("testUser");

        when(bizConfig.isNamespaceLockSwitchOff()).thenReturn(false);

        Namespace mockNamespace = new Namespace();
        when(namespaceService.findOne(appId, clusterName, namespaceName)).thenReturn(mockNamespace);

        when(itemService.findOne(itemId)).thenReturn(new Item());

        when(namespaceLockService.findLock(mockNamespace.getId())).thenReturn(null);

        lockAspect.requireLockAdvice(appId, clusterName, namespaceName, itemId, item);

        verify(namespaceLockService, times(1)).tryLock(argThat(lock ->
                lock.getNamespaceId() == mockNamespace.getId() && lock.getDataChangeCreatedBy().equals("testUser")));
    }

    @Test
    public void testRequireLockAdviceForUpdateByChangeSet() {
        String appId = "testApp";
        String clusterName = "testCluster";
        String namespaceName = "testNamespace";
        ItemChangeSets changeSet = new ItemChangeSets();
        changeSet.setDataChangeLastModifiedBy("testUser");

        when(bizConfig.isNamespaceLockSwitchOff()).thenReturn(false);

        Namespace mockNamespace = new Namespace();
        when(namespaceService.findOne(appId, clusterName, namespaceName)).thenReturn(mockNamespace);

        when(namespaceLockService.findLock(mockNamespace.getId())).thenReturn(null);

        lockAspect.requireLockAdvice(appId, clusterName, namespaceName, changeSet);

        verify(namespaceLockService, times(1)).tryLock(argThat(lock ->
                lock.getNamespaceId() == mockNamespace.getId() && lock.getDataChangeCreatedBy().equals("testUser")));
    }
}


