package me.jho5245.mario.sounds;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Sound
{
	private int bufferId;
	private int sourceId;
	private String filePath;

	private boolean isPlaying;

	public Sound(String filePath, boolean loop)
	{
		this.filePath = filePath;
		// allocate space to store the return info from stb
		stackPush();
		IntBuffer channelsBuffer = stackMallocInt(1);
		stackPush();
		IntBuffer sampleRateBuffer = stackMallocInt(1);

		ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(filePath, channelsBuffer, sampleRateBuffer);
		if (rawAudioBuffer == null)
		{
			System.out.println("Failed to load raw audio file: " + filePath);
			stackPop();
			stackPop();
			return;
		}

		// retrieve extra info in the buffered by stb
		int channels = channelsBuffer.get(0);
		int sampleRate = sampleRateBuffer.get(0);
		// free
		stackPop();
		stackPop();

		// find the correct openAL format
		int format = -1;
		switch (channels)
		{
			case 1 -> format = AL_FORMAT_MONO16;
			case 2 -> format = AL_FORMAT_STEREO16;
		}

		bufferId = alGenBuffers();
		alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

		// generate the source
		sourceId = alGenSources();
		alSourcei(sourceId, AL_BUFFER, bufferId);
		alSourcei(sourceId, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
		alSourcei(sourceId, AL_POSITION, 0);
		alSourcef(sourceId, AL_GAIN, 0.3f);

		// free stb raw audio buffer
		free(rawAudioBuffer);
	}

	public void delete()
	{
		alDeleteSources(sourceId);
		alDeleteBuffers(bufferId);
	}

	public void play()
	{
		int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
		if (state == AL_STOPPED)
		{
			isPlaying = false;
			alSourcei(sourceId, AL_POSITION, 0);
		}

		if (!isPlaying)
		{
			alSourcePlay(sourceId);
			isPlaying = true;
		}
	}

	public void stop()
	{
		if (isPlaying)
		{
			alSourceStop(sourceId);
			isPlaying = false;
		}
	}

	public String getFilePath()
	{
		return filePath;
	}

	public boolean isPlaying()
	{
		int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
		if (state == AL_STOPPED)
		{
			isPlaying = false;
		}
		return isPlaying;
	}
}
