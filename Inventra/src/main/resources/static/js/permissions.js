function applyPermissions(permissions){
        function  hideElement(elementId) {
                const element = document.getElementById(elementId);
                 if(element)
                 element.style.display = 'none';
        }
}

        if(!permissions.include('group_view')) {
           hideElement('groupViewButton')
        }
        if(!permissions.include('group_add')) {
           hideElement('groupAddButton')
        }
        if(!permissions.include('group_update')) {
           hideElement('groupUpdateButton')
        }
        if(!permissions.include('group_delete')) {
           hideElement('groupDeleteButton')
        }
        if(!permissions.include('subAdmin_view')) {
           hideElement('subAdminViewButton')
        }
        if(!permissions.include('subAdmin_add')) {
           hideElement('subAdminAddButton')
        }
        if(!permissions.include('subAdmin_update')) {
           hideElement('subAdminUpdateButton')
        }
        if(!permissions.include('subAdmin_delete')) {
           hideElement('subAdminDeleteButton')
        }
        if(!permissions.include('product_view')) {
           hideElement('productViewButton')
        }
        if(!permissions.include('product_add')) {
           hideElement('productAddButton')
        }
        if(!permissions.include('product_update')) {
           hideElement('productUpdateButton')
        }
        if(!permissions.include('product_delete')) {
           hideElement('productDeleteButton')
        }
        if(!permissions.include('user_view')) {
           hideElement('userViewButton')
        }
        if(!permissions.include('user_add')) {
           hideElement('userAddButton')
        }
        if(!permissions.include('user_update')) {
           hideElement('userUpdateButton')
        }
        if(!permissions.include('user_delete')) {
           hideElement('userDeleteButton')
        }

function fetchUserPermission(){
        fetch('/user/permissions')
        .then(response => {
        if(!response.ok){
                throw new Error ('Failed to fetch permission: ' +  response.status);
                }
                return response.json();
                })
                .then(data => {
                if ( data && data.permissions) {
                        applyPermissions(data.permissions);
                        }else{
                                console.error('Invalid permission data:', data);
                                 }
                               })
                               .console(error => {
                               console.error('Error fetching permission:', error);
                               });

}

    window.onload = function() {
        fetchUserPermission();
    };
