package com.graduate.training.service;

import com.graduate.training.entities.Order;
import com.graduate.training.entities.Strategy;

import java.time.LocalDateTime;
import java.util.List;

public class TwoMovingAveragesAlgo extends StrategyAlgo {

    private int shortPeriod;
    private int longPeriod;
    private double longAverage = 0;
    private double shortAverage = 0;

    public TwoMovingAveragesAlgo(Strategy strategy, int shortPeriod, int longPeriod) {
        super(strategy);
        this.shortPeriod = shortPeriod;
        this.longPeriod = longPeriod;
    }

    Order runStrategy(PriceFeedService priceFeed) {
        Order newOrder = null;
        String ticker = getTicker();
        priceFeed.register(ticker);


        List<Double> currentShortRange = priceFeed.getPriceRange(ticker, shortPeriod);
        List<Double> currentLongRange = priceFeed.getPriceRange(ticker, longPeriod);
        if (currentLongRange.size() < longPeriod) {
            System.out.println("warming up, currentLongRangeSize(): " + currentLongRange.size());
            return newOrder;
        }

        //get Averages
        double currentShortAverage = getAverage(currentShortRange);
        double currentLongAverage = getAverage(currentLongRange);
        System.out.println("shortAverage: " + currentShortAverage + ", longAverage: " + currentLongAverage);

        double price = priceFeed.getCurrentPrice(ticker);
        if ((shortAverage > longAverage)&&(currentShortAverage < currentLongAverage)){
            newOrder = new Order(false, price,100, ticker, LocalDateTime.now(), getId());
            System.out.println("New Sell Order:" + newOrder.toString());
        }
        if ((longAverage > shortAverage)&&(currentLongAverage < currentShortAverage)) {
            newOrder = new Order(true, price, 100, ticker, LocalDateTime.now(), getId());
            System.out.println("New Buy Order:" + newOrder.toString());
        }

        shortAverage = currentShortAverage;
        longAverage = currentLongAverage;
        return newOrder;
    }
    private double getAverage (List<Double> range){
        if (range.size() == 0) {
            return 0;
        }
        double sum = 0.0;
        for (double price : range){
            sum += price;
        }
        return sum/range.size();
    }
}