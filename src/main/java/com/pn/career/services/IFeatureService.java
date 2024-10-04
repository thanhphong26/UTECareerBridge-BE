package com.pn.career.services;

import com.pn.career.models.Feature;

import java.util.List;

public interface IFeatureService {
    List<Feature> getAllFeatures();
    Feature getFeatureById(Integer featureId);
    Feature createFeature(Feature feature);
    Feature updateFeature(Integer featureId, Feature feature);
    void deleteFeature(Integer featureId);
}
