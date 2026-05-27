import { Injectable, NotFoundException } from '@nestjs/common';
import { ImoveisRepository } from './imoveis.repository';

@Injectable()
export class ImoveisService {
  constructor(private readonly repo: ImoveisRepository) {}

  findAll(imobiliariaId: string) {
    return this.repo.findAll(imobiliariaId);
  }

  async findById(id: string, imobiliariaId: string) {
    const imovel = await this.repo.findById(id, imobiliariaId);
    if (!imovel) throw new NotFoundException(`Imóvel ${id} não encontrado`);
    return imovel;
  }
}
