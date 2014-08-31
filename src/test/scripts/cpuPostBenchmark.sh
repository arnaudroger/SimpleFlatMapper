    echo "ondemand" | sudo tee /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor;
    echo "800000" | sudo tee /sys/devices/system/cpu/cpu*/cpufreq/scaling_min_freq;
    echo "2668000" | sudo tee /sys/devices/system/cpu/cpu*/cpufreq/scaling_max_freq
