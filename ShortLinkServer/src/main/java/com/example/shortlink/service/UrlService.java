package com.example.shortlink.service;

import com.example.shortlink.model.Url;
import com.example.shortlink.model.UrlDto;
import org.springframework.stereotype.Service;

@Service
public interface UrlService {
    public Url generateShortLink(UrlDto UrlDto);
    public Url persistShortLink(Url url);
    public Url getEncodedUrl(String url);
    public void deleteShortlink(Url url);
}
