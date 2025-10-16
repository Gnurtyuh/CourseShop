package com.javaweb.web.service;

import com.javaweb.web.entity.Topups;
import com.javaweb.web.entity.Users;
import com.javaweb.web.repository.TopupsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TopupsService {
    @Autowired
    private TopupsRepo topupsRepo;
    @Autowired
    private UsersService userService;
    public Topups addTopups(Topups topups) {
        return topupsRepo.save(topups);
    }
    public List<Topups> getAllTopups() {
        List<Topups> topups = topupsRepo.findAll();
        for(Topups topup : topups){
            topup.setUser(userService.getUserById(topup.getUser().getId()));
        }
        return topups;
    }
    public List<Topups> getTopupsByUserId(int userId) {
        return topupsRepo.findByUserId(userId);
    }

    public boolean updateTopups(int id, String status) {
        Optional<Topups> optional = topupsRepo.findById(id);
        if (optional.isEmpty()) return false;
        Topups topups = optional.get();
        userService.increaseBalance(topups.getUser().getId(), topups.getAmount());
        topups.setStatus(status);
        topupsRepo.save(topups);
        return true;
    }
    public void deleteTopups(int id) {
        topupsRepo.deleteById(id);
    }
    public List<Topups> findByUserId(int userId) {
        return topupsRepo.findByUserId(userId);
    }
}
