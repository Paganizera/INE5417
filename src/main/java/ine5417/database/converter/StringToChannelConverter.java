package ine5417.database.converter;
import ine5417.database.Channel;
import ine5417.database.ChannelService;
import ine5417.exceptions.ChannelNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StringToChannelConverter implements Converter<String, Channel> {

    private final ChannelService channelService;

    public StringToChannelConverter(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public Channel convert(@NotNull String source) {
        if (source.isEmpty()) {
            return null;
        }

        UUID channelId = UUID.fromString(source);
        Channel channel = channelService.findById(channelId);
        if (channel == null) {
            throw new ChannelNotFoundException("Channel not found with id: " + source);
        }
        return channel;
    }
}