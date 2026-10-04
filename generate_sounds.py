"""Generate simple WAV sound effects for the game."""
import struct
import math
import os

SAMPLE_RATE = 22050
OUTPUT_DIR = r"c:\Users\George\Desktop\Android Projects\God of Nose Sneeze and Thunder\app\src\main\res\raw"

def write_wav(filename, samples):
    """Write a list of float samples (-1..1) to a WAV file."""
    path = os.path.join(OUTPUT_DIR, filename)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    
    # Convert to 16-bit PCM
    data = b''
    for s in samples:
        s = max(-1.0, min(1.0, s))
        data += struct.pack('<h', int(s * 32767))
    
    with open(path, 'wb') as f:
        # RIFF header
        f.write(b'RIFF')
        f.write(struct.pack('<I', 36 + len(data)))
        f.write(b'WAVE')
        # fmt chunk
        f.write(b'fmt ')
        f.write(struct.pack('<I', 16))  # chunk size
        f.write(struct.pack('<H', 1))   # PCM
        f.write(struct.pack('<H', 1))   # mono
        f.write(struct.pack('<I', SAMPLE_RATE))
        f.write(struct.pack('<I', SAMPLE_RATE * 2))  # byte rate
        f.write(struct.pack('<H', 2))   # block align
        f.write(struct.pack('<H', 16))  # bits per sample
        # data chunk
        f.write(b'data')
        f.write(struct.pack('<I', len(data)))
        f.write(data)

def generate_sneeze(duration=0.3, pitch_variation=1.0):
    """Generate a sneeze sound: noise burst with pitch sweep."""
    n = int(SAMPLE_RATE * duration)
    samples = []
    for i in range(n):
        t = i / SAMPLE_RATE
        # Frequency sweep from high to low
        freq = (800 + 1200 * math.exp(-t * 8)) * pitch_variation
        # Mix of noise and tone
        noise = (hash(i) % 1000) / 500.0 - 1.0
        tone = math.sin(2 * math.pi * freq * t)
        envelope = math.exp(-t * 4) * (1 - math.exp(-t * 30))
        sample = (noise * 0.6 + tone * 0.4) * envelope * 0.8
        samples.append(sample)
    return samples

def generate_crit(duration=0.4):
    """Generate a critical hit sound: bright metallic ping."""
    n = int(SAMPLE_RATE * duration)
    samples = []
    for i in range(n):
        t = i / SAMPLE_RATE
        # Bright high-pitched tone with harmonics
        tone = (math.sin(2 * math.pi * 1200 * t) * 0.5 +
                math.sin(2 * math.pi * 2400 * t) * 0.3 +
                math.sin(2 * math.pi * 3600 * t) * 0.2)
        envelope = math.exp(-t * 6) * (1 - math.exp(-t * 50))
        samples.append(tone * envelope * 0.9)
    return samples

def generate_destroy(duration=0.5):
    """Generate an item destruction sound: crash/explosion."""
    n = int(SAMPLE_RATE * duration)
    samples = []
    for i in range(n):
        t = i / SAMPLE_RATE
        # Noise burst with low frequency rumble
        noise = (hash(i * 7) % 1000) / 500.0 - 1.0
        rumble = math.sin(2 * math.pi * 80 * t) * 0.3
        crash = math.sin(2 * math.pi * 400 * t) * math.exp(-t * 10) * 0.5
        envelope = math.exp(-t * 3) * (1 - math.exp(-t * 40))
        sample = (noise * 0.5 + rumble + crash) * envelope * 0.9
        samples.append(sample)
    return samples

def generate_no_stamina(duration=0.2):
    """Generate a 'no stamina' sound: short low buzz."""
    n = int(SAMPLE_RATE * duration)
    samples = []
    for i in range(n):
        t = i / SAMPLE_RATE
        # Low buzzy tone
        tone = math.sin(2 * math.pi * 150 * t) * 0.5
        buzz = (hash(i * 3) % 500) / 250.0 - 1.0
        envelope = math.exp(-t * 15)
        sample = (tone * 0.5 + buzz * 0.5) * envelope * 0.7
        samples.append(sample)
    return samples

def generate_frenzy(duration=0.6):
    """Generate a frenzy activation sound: ascending sparkle."""
    n = int(SAMPLE_RATE * duration)
    samples = []
    for i in range(n):
        t = i / SAMPLE_RATE
        # Ascending tone
        freq = 400 + 1200 * (t / duration)
        tone = (math.sin(2 * math.pi * freq * t) * 0.4 +
                math.sin(2 * math.pi * freq * 2 * t) * 0.3 +
                math.sin(2 * math.pi * freq * 3 * t) * 0.2)
        envelope = math.sin(math.pi * t / duration) * 0.8
        samples.append(tone * envelope)
    return samples

def generate_boss_attack(duration=0.5):
    """Generate a boss attack sound: deep growl."""
    n = int(SAMPLE_RATE * duration)
    samples = []
    for i in range(n):
        t = i / SAMPLE_RATE
        # Low growl with modulation
        freq = 100 + 50 * math.sin(2 * math.pi * 5 * t)
        tone = math.sin(2 * math.pi * freq * t) * 0.4
        sub = math.sin(2 * math.pi * 50 * t) * 0.3
        noise = (hash(i * 11) % 800) / 400.0 - 1.0
        envelope = math.exp(-t * 4) * (1 - math.exp(-t * 20))
        sample = (tone + sub + noise * 0.3) * envelope * 0.8
        samples.append(sample)
    return samples

def generate_boss_phase(duration=0.7):
    """Generate a boss phase transition sound: ominous chord."""
    n = int(SAMPLE_RATE * duration)
    samples = []
    for i in range(n):
        t = i / SAMPLE_RATE
        # Minor chord
        c1 = math.sin(2 * math.pi * 220 * t)  # A
        c2 = math.sin(2 * math.pi * 261.63 * t)  # C
        c3 = math.sin(2 * math.pi * 329.63 * t)  # E
        tone = (c1 * 0.3 + c2 * 0.3 + c3 * 0.3)
        envelope = math.sin(math.pi * t / duration) * 0.7
        samples.append(tone * envelope)
    return samples

def generate_achievement(duration=0.8):
    """Generate an achievement unlock sound: triumphant fanfare."""
    n = int(SAMPLE_RATE * duration)
    samples = []
    for i in range(n):
        t = i / SAMPLE_RATE
        # Major chord arpeggio
        phase = t / duration
        if phase < 0.33:
            freq = 523.25  # C5
        elif phase < 0.66:
            freq = 659.25  # E5
        else:
            freq = 783.99  # G5
        tone = (math.sin(2 * math.pi * freq * t) * 0.4 +
                math.sin(2 * math.pi * freq * 2 * t) * 0.2)
        envelope = math.sin(math.pi * t / duration) * 0.8
        samples.append(tone * envelope)
    return samples

def generate_button_click(duration=0.05):
    """Generate a UI button click sound."""
    n = int(SAMPLE_RATE * duration)
    samples = []
    for i in range(n):
        t = i / SAMPLE_RATE
        click = math.sin(2 * math.pi * 1000 * t) * math.exp(-t * 80) * 0.5
        samples.append(click)
    return samples

# Generate all sounds
print("Generating sneeze sounds...")
for i in range(1, 11):
    pitch = 0.8 + (i * 0.04)  # Vary pitch for each sneeze
    samples = generate_sneeze(0.25 + (i * 0.01), pitch)
    write_wav(f"sneeze{i}.wav", samples)
    print(f"  sneeze{i}.wav")

print("Generating special sounds...")
write_wav("crit.wav", generate_crit())
print("  crit.wav")
write_wav("destroy.wav", generate_destroy())
print("  destroy.wav")
write_wav("nostamina.wav", generate_no_stamina())
print("  nostamina.wav")
write_wav("frenzy.wav", generate_frenzy())
print("  frenzy.wav")
write_wav("boss_attack.wav", generate_boss_attack())
print("  boss_attack.wav")
write_wav("boss_phase.wav", generate_boss_phase())
print("  boss_phase.wav")
write_wav("achievement.wav", generate_achievement())
print("  achievement.wav")
write_wav("button_click.wav", generate_button_click())
print("  button_click.wav")

print("\nDone! All sounds generated.")
