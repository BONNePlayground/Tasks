//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.adapters;

import java.util.TimeZone;

import world.bentobox.bentobox.database.objects.adapters.AdapterInterface;


/**
 * This class deserializes TimeZone object.
 */
public class TimeZoneAdapter implements AdapterInterface<TimeZone, String>
{
    @Override
    public TimeZone deserialize(Object object)
    {
        TimeZone timeZone = TimeZone.getDefault();

        if (object instanceof String)
        {
            timeZone = TimeZone.getTimeZone((String) object);
        }

        return timeZone;
    }


    @Override
    public String serialize(Object object)
    {
        TimeZone timeZone = object == null ? TimeZone.getDefault() : (TimeZone) object;
        return timeZone.toZoneId().getId();
    }
}
