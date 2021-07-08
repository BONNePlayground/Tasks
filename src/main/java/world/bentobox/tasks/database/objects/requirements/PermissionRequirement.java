//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.requirements;


import com.google.gson.annotations.Expose;


/**
 * Permission requirement.
 */
public class PermissionRequirement implements Requirement
{
    /**
     * Gets permission.
     *
     * @return the permission
     */
    public String getPermission()
    {
        return permission;
    }


    /**
     * Sets permission.
     *
     * @param permission the permission
     */
    public void setPermission(String permission)
    {
        this.permission = permission;
    }


    /**
     * String that stores permission.
     */
    @Expose
    private String permission;
}
