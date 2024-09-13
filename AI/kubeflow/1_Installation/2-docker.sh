sudo apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release

## key error 나면 PUB_KEY 자리에 KEY 넣어서
# sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys <PUB_KEY>

sudo install -m 0755 -d /etc/apt/keyrings

sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
# sudo chmod a+r /etc/apt/keyrings/docker.asc

echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt-get update -y
sudo apt-get install -y docker-ce docker-ce-cli containerd.io
sudo docker run hello-world
sudo usermod -aG docker $USER && newgrp docker
sudo service docker restart