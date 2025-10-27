package ine5417.database;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChannelService {
    private final ChannelRepository channelRepository;

    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }


    @Cacheable(value = "channel", key = "#id")
    public Channel findById(UUID id) {
        return channelRepository.findChannelById(id);
    }

    public Channel createChannel(String name,
                                 String description,
                                 String email) {
        Channel channel = new Channel();
        channel.setId(UUID.randomUUID());
        channel.setName(name);
        channel.setDescription(description);
        channel.setEmail(email);
        return this.channelRepository.save(channel);
    }
}
