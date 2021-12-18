import math
import numpy
import gym

env = gym.make('MountainCar-v0')

env.reset()

pos_states = 18
vel_states = 72

def obs_to_state(env, obs):
	env_low = env.observation_space.low
	env_high = env.observation_space.high
	env_indx0 = (env_high[0] - env_low[0]) / pos_states
	env_indx1 = (env_high[1] - env_low[1]) / vel_states
	pos0 = int((obs[0] - env_low[0])/env_indx0)
	vel1 = int((obs[1] - env_low[1])/env_indx1)
	return pos0, vel1

episode_reward = 0

total_states = vel_states*(pos_states-1)+vel_states

Q = numpy.zeros((total_states, 3))

score_list = [None] * 100
tries_list = [None] * 100

max_height = -0.4
prev_height = -0.4

for i_episode in range(2000):
	episode_reward = 0
	#max_height = -0.4
	#prev_height = -0.4
	observation = env.reset()
	alpha = max(0.002, math.pow(0.9,(math.floor(i_episode/100))))
	gamma = 0.1
	for t_attempt in range(200):
		env.render()

		pos, vel = obs_to_state(env, observation)

		#rand_num = max(0.1, 0.05-((math.floor(i_episode/100))*0.01))

		if (numpy.random.uniform(0, 1) < 0.05):
			action = env.action_space.sample()
		else:
			act_left = Q[vel_states*pos+vel][0]
			act_neutral = Q[vel_states*pos+vel][1]
			act_right = Q[vel_states*pos+vel][2]
			total_act = act_left+act_neutral+act_right

			if(total_act==0 or (act_left==act_neutral and act_neutral==act_right)):
				p_left = 0.35
				p_neutral = 0.3
				p_right = 0.35
			else:
				if(act_left>=act_neutral and act_left>=act_right):
					p_left = 1
					p_neutral = 0
					p_right = 0
				elif(act_neutral>=act_left and act_neutral>=act_right):
					p_left = 0
					p_neutral = 1
					p_right = 0
				elif(act_right>=act_left and act_right>=act_neutral):
					p_left = 0
					p_neutral = 0
					p_right = 1
			

			action = numpy.random.choice(env.action_space.n, p=[p_left, p_neutral, p_right])

		observation, reward, done, info = env.step(action)
		nm_reward = reward

		if(observation[0]>max_height):
			prev_height = max_height
			max_height = observation[0]
			reward = reward + 0.2
		elif(observation[0]>prev_height and observation[0]<max_height):
			prev_height = observation[0]
			reward = reward + 0.1
		elif(observation[0]<prev_height and observation[0]>-0.5):
			reward = reward - 0.1
		else:
			reward = reward

		reward = reward + (abs(observation[1])*5)

		if(observation[0] > -0.5 or observation[1]>0):
			episode_reward = episode_reward + nm_reward

		pos_, vel_ = obs_to_state(env, observation)

		if(observation[0]>0.5):
			#reward = reward + 1
			#if(t_attempt<110):
				#reward = reward + 1
			print("Success! Episode "+(str(i_episode))+": We got a reward of "+str(episode_reward)+" with "+(str(t_attempt+1))+" actions.")
			#Q[vel_states*pos+vel][action] = Q[vel_states*pos+vel][action] + alpha * (reward + gamma *  numpy.max(Q[vel_states*pos_+vel_]) - Q[vel_states*pos+vel][action])
			break
		elif(t_attempt==199):
			episode_reward = -200
			print("Failed! Episode "+(str(i_episode))+": Max height reached: "+(str(max_height))+". We got a reward of "+str(episode_reward)+" with "+(str(t_attempt+1))+" actions.")

		Q[vel_states*pos+vel][action] = Q[vel_states*pos+vel][action] + alpha * (reward + gamma *  numpy.max(Q[vel_states*pos_+vel_]) - Q[vel_states*pos+vel][action])


	indx = i_episode%100
	score_list[indx] = episode_reward
	tries_list[indx] = t_attempt+1
	if(i_episode>=99):
		if(sum(score_list)>-11000):
			avg_score = sum(score_list)/100
			avg_tries = sum(tries_list)/100
			print("Success! We got an average score of "+str(avg_score)+" with an average of "+str(avg_tries)+" actions in 100 consecutive trials. We needed "+str((i_episode+1))+" trials to achieve this.")
			break




# ONLY FOR TESTING OUR Q TABLE:

score_list = [None] * 100
tries_list = [None] * 100

for i_episode in range(100):
	episode_reward = 0
	observation = env.reset()
	for t_attempt in range(200):
		env.render()

		pos, vel = obs_to_state(env, observation)

		act_left = Q[vel_states*pos+vel][0]
		act_neutral = Q[vel_states*pos+vel][1]
		act_right = Q[vel_states*pos+vel][2]
		total_act = act_left+act_neutral+act_right

		if(total_act==0 or (act_left==act_neutral and act_neutral==act_right)):
			p_left = 0.35
			p_neutral = 0.3
			p_right = 0.35
		else:
			if(act_left>=act_neutral and act_left>=act_right):
				p_left = 1
				p_neutral = 0
				p_right = 0
			elif(act_neutral>=act_left and act_neutral>=act_right):
				p_left = 0
				p_neutral = 1
				p_right = 0
			elif(act_right>=act_left and act_right>=act_neutral):
				p_left = 0
				p_neutral = 0
				p_right = 1
			

		action = numpy.random.choice(env.action_space.n, p=[p_left, p_neutral, p_right])

		observation, reward, done, info = env.step(action)
		nm_reward = reward

		if(observation[0] > -0.5 or observation[1]>0):
			episode_reward = episode_reward + nm_reward

		pos_, vel_ = obs_to_state(env, observation)

		if(observation[0]>0.5):
			print("Success! Episode "+(str(i_episode))+": We got a reward of "+str(episode_reward)+" with "+(str(t_attempt+1))+" actions.")
			break
		elif(t_attempt==199):
			episode_reward = -200
			print("Failed! Episode "+(str(i_episode))+": Max height reached: "+(str(max_height))+". We got a reward of "+str(episode_reward)+" with "+(str(t_attempt+1))+" actions.")


	indx = i_episode%100
	score_list[indx] = episode_reward
	tries_list[indx] = t_attempt+1
	if(i_episode>=99):
		if(sum(score_list)>-11000):
			avg_score = sum(score_list)/100
			avg_tries = sum(tries_list)/100
			print("Success! We got an average score of "+str(avg_score)+" with an average of "+str(avg_tries)+" actions in 100 consecutive trials. We needed "+str((i_episode+1))+" trials to achieve this.")
			break


avg_score = sum(score_list)/100
avg_tries = sum(tries_list)/100
print("We got an average score of "+str(avg_score)+" with an average of "+str(avg_tries)+" actions in 100 consecutive trials!")